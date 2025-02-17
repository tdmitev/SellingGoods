import org.example.config.ExecutorServiceConfig;
import org.example.model.Cart;
import org.example.model.Customer;
import org.example.model.Product;
import org.example.parallelProcessing.ConcurrentPurchaseProcessing;
import org.example.tasks.InventoryManager;
import org.example.tasks.PurchaseTask;
import org.example.threadCommunication.StoreInventory;
import org.junit.jupiter.api.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StoreTests {

    private StoreInventory inventory;
    private ExecutorService executorService;

    @BeforeEach
    void setup() {
        inventory = new StoreInventory();
        inventory.addProduct(new Product(1, "Apple", 50, 0.5));
        inventory.addProduct(new Product(2, "Banana", 30, 0.3));
        inventory.addProduct(new Product(3, "Orange", 20, 0.7));

        executorService = Executors.newFixedThreadPool(4);
    }

    @AfterAll
    void cleanup() {
        executorService.shutdown();
    }

    @Test
    void testAddProductToCart() {
        Cart cart = new Cart();
        Product apple = inventory.getProduct(1);
        assertNotNull(apple);

        cart.addProduct(new Product(apple.getId(), apple.getName(), 5, apple.getPrice()), apple.getQuantity());
        assertEquals(1, cart.getProducts().size());
        assertEquals(2.5, cart.calculateTotalPrice());
    }

    @Test
    void testInsufficientStock() {
        Cart cart = new Cart();
        Product orange = inventory.getProduct(3);
        assertNotNull(orange);

        cart.addProduct(new Product(orange.getId(), orange.getName(), 25, orange.getPrice()), orange.getQuantity());
        assertEquals(0, cart.getProducts().size());
    }

    @Test
    void testPurchaseCart() {
        Customer customer = new Customer(1, "John Doe");
        Product apple = inventory.getProduct(1);
        Product banana = inventory.getProduct(2);

        customer.getCart().addProduct(new Product(apple.getId(), apple.getName(), 5, apple.getPrice()), apple.getQuantity());
        customer.getCart().addProduct(new Product(banana.getId(), banana.getName(), 3, banana.getPrice()), banana.getQuantity());

        boolean success = inventory.purchaseCart(customer);
        assertTrue(success);

        assertEquals(45, inventory.getProduct(1).getQuantity());
        assertEquals(27, inventory.getProduct(2).getQuantity());
    }

    @Test
    void testConcurrentPurchases() throws InterruptedException {
        List<Customer> customers = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Customer customer = new Customer(i, "Customer " + i);
            Product apple = inventory.getProduct(1);
            if (apple != null) {
                customer.getCart().addProduct(new Product(apple.getId(), apple.getName(), 2, apple.getPrice()), apple.getQuantity());
            }
            customers.add(customer);
        }

        ConcurrentPurchaseProcessing purchaseProcessing = new ConcurrentPurchaseProcessing(inventory);
        purchaseProcessing.processPurchases(customers);

        System.out.println("Remaining stock for Apple: " + inventory.getProduct(1).getQuantity());

        assertEquals(40, inventory.getProduct(1).getQuantity());
    }

    @Test
    void testExecutorServiceConfig() {
        ExecutorService executor = ExecutorServiceConfig.getExecutorService();
        assertNotNull(executor);

        executor.submit(() -> System.out.println("Test task executed"));
        ExecutorServiceConfig.shutdown();
        assertTrue(executor.isShutdown());
    }

    @Test
    void testInventoryManagerInitialization() {
        InventoryManager manager = new InventoryManager(inventory);
        List<Product> additionalProducts = List.of(
                new Product(4, "Grapes", 15, 2.0),
                new Product(5, "Peach", 10, 1.5)
        );
        manager.initializeInventory(additionalProducts);
        assertEquals(5, inventory.getProduct(5).getId());
    }

    @Test
    void testPurchaseTask() throws Exception {
        Customer customer = new Customer(1, "Test Customer");
        Product apple = inventory.getProduct(1);

        if (apple != null) {
            customer.getCart().addProduct(new Product(apple.getId(), apple.getName(), 2, apple.getPrice()), apple.getQuantity());
        }

        PurchaseTask task = new PurchaseTask(customer, inventory);
        Future<Void> future = executorService.submit(task);
        future.get();

        System.out.println("Inventory after task: " + inventory.getProduct(1));

        assertEquals(48, inventory.getProduct(1).getQuantity());
    }

    @Test
    void testStoreInventoryConcurrency() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                inventory.addProduct(new Product(10 + i, "Product" + i, 100, 1.0));
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                inventory.getProduct(10 + i);
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Inventory after concurrency test:");
        inventory.printInventory();

        assertNotNull(inventory.getProduct(13));
    }
}
