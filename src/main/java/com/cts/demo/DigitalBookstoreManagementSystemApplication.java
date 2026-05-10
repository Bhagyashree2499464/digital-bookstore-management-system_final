package com.cts.demo;

import com.cts.demo.exception.*;
import com.cts.demo.model.*;
import com.cts.demo.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class DigitalBookstoreManagementSystemApplication
        implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(DigitalBookstoreManagementSystemApplication.class);

    private final UserService userService;
    private final BookService bookService;
    private final AuthorService authorService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final CategoryService categoryService;
    private final InventoryService inventoryService;
    private final ReviewService reviewService;

    private User loggedInUser;

    public DigitalBookstoreManagementSystemApplication(
            UserService userService,
            AuthorService authorService,
            BookService bookService,
            OrderService orderService,
            OrderItemService orderItemService,
            CategoryService categoryService,
            InventoryService inventoryService,
            ReviewService reviewService) {
        this.userService = userService;
        this.authorService = authorService;
        this.bookService = bookService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.categoryService = categoryService;
        this.inventoryService = inventoryService;
        this.reviewService = reviewService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DigitalBookstoreManagementSystemApplication.class, args);
    }

    @Override
    public void run(String... args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            log.info("""
                    
                    ===============================
                    DIGITAL BOOKSTORE
                    ===============================
                    1. Register
                    2. Login
                    0. Exit
                    Enter choice:
                    """);

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                log.warn("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1 -> register(scanner);
                case 2 -> login(scanner);
                case 0 -> {
                    log.info("Exiting application...");
                    return; //clean shutdown
                }
                default -> log.warn("Invalid choice");
            }
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        );
    }

    private void register(Scanner scanner) {

        log.info("Enter Name:");
        String name = scanner.nextLine();

        log.info("Enter Email:");
        String email = scanner.nextLine().trim();

        if (!isValidEmail(email)) {
            log.info("Invalid email format. Please enter a valid email address.");
            return;
        }

        log.info("Enter Password:");
        String password = scanner.nextLine();

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("CUSTOMER");

        try {
            userService.registerUser(user);
            log.info("Registration successful. Please login.");
        } catch (RuntimeException ex) {
            log.info(ex.getMessage());
        }
    }
    private void login(Scanner scanner) {

        log.info("Enter Email:");
        String email = scanner.nextLine().trim();

        if (!isValidEmail(email)) {
            log.info("Invalid email format. Please enter a valid email address.");
            return;
        }

        log.info("Enter Password:");
        String password = scanner.nextLine();

        try {
            if (!userService.login(email, password, "CUSTOMER")
                    && !userService.login(email, password, "ADMIN")) {

                log.warn("Invalid email or password");
                return;
            }

            loggedInUser = userService.getUserByEmail(email);
            log.info("Welcome, {}", loggedInUser.getName());

            if ("CUSTOMER".equals(loggedInUser.getRole())) {
                userDashboard(scanner);
            } else {
                adminDashboard(scanner);
            }

        } catch (RuntimeException ex) {
            log.info(ex.getMessage());
        }
    }
    /*
        ==================================================================================================
        ==================================================================================================
                                             USER DASHBOARD
        ==================================================================================================
        ==================================================================================================

    */
    private void userDashboard(Scanner scanner) {

        while (loggedInUser != null) {
            log.info("""
                    
                    ===========================
                    USER DASHBOARD
                    ===========================
                    1. Book Catalog(Search Books)
                    2. View My Orders
                    3. Order books
                    4. Give Review
                    5. view Profile
                    6. cancel orders(Please give id)
                    7. Update Order Item
                    8. Delete Order Item
                    9. Logout
                    Enter choice:
                    """);

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                log.warn("Invalid input");
                continue;
            }

            switch (choice) {
                case 1 -> bookCatalog(scanner);
                case 2 -> viewMyOrders(scanner);
                case 3 -> orderBooksFlow(scanner);
                case 4 -> reviewBooksFlow(scanner);
                case 5 -> viewProfile(loggedInUser);
                case 6 -> cancelOrder(scanner);
                case 7 -> updateOrderItem(scanner);
                case 8 -> deleteOrderItem(scanner);
                case 9 -> {
                    loggedInUser = null;
                    log.info("Logged out successfully");
                }
                default -> log.warn("Invalid choice");
            }
        }
    }

    private void viewProfile(User loggedInUser){
        if(loggedInUser == null){
            log.info("User does not exist.");
            return;
        }
        log.info("""
               
               
               Your Details:: 
               
               Name: {}
               
               Email: {} 
               """, loggedInUser.getName(), loggedInUser.getEmail());
    }

    /*
        ==========================================================================
                                BOOK CATALOG
        ==========================================================================

    */
    private void bookCatalog(Scanner scanner) {

        boolean back = false;

        while (!back) {
            log.info("""
                    
                    ===========================
                    BOOK CATALOG
                    ===========================
                    1. Search Books
                    2. See All Books
                    3. See All Authors
                    4. See All Categories
                    5. Back to Dashboard
                    Enter choice:
                    """);

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                log.warn("Invalid input");
                continue;
            }

            switch (choice) {
                case 1 -> searchBooks(scanner);
                case 2 -> seeAllBooks();
                case 3 -> seeAllAuthors();
                case 4 -> seeAllBookCategories();
                case 5 -> back = true;
                default -> log.warn("Invalid choice");
            }
        }
    }

    private void displayBooks(List<Book> books) {
        if (books.isEmpty()) {
            log.info("No books found.");
            seeAllBooks();
            return;
        }

        books.forEach(book ->
                log.info(
                        "ID: {} | Title: {} | Price: {}",
                        book.getBookId(),
                        book.getTitle(),
                        book.getPrice()
                )
        );
    }

    private String displayBook(Book book) {
        if (book == null) {
            return "No books found.";
        }

        return (
                "ID: "
                        + book.getBookId()+ " | Title: "
                        + book.getTitle() +"  | Price: "
                        + book.getPrice()
        );
    }

    private void searchBooks(Scanner scanner) {

        log.info("Enter book title (press Enter to skip): ");
        String title = scanner.nextLine().trim();

        log.info("Enter author name (press Enter to skip): ");
        String author = scanner.nextLine().trim();

        log.info("Enter category (Databases/Programming/Software Engineering/Web Development) (press Enter to skip): ");
        String category = scanner.nextLine().trim();

        List<Book> books = new ArrayList<>();

        boolean hasTitle = !title.isEmpty();
        boolean hasAuthor = !author.isEmpty();
        boolean hasCategory = !category.isEmpty();


        try {
            if (hasTitle && hasAuthor && hasCategory) {
                books = bookService.findByCategoryAndTitleAndAuthorName(category, title, author);
            } else if (hasTitle && hasAuthor) {
                books = bookService.getBooksByTitleAndAuthorName(title, author);
            } else if (hasTitle && hasCategory) {
                books = bookService.findByCategoryAndTitle(category, title);
            } else if (hasAuthor && hasCategory) {
                books = bookService.findByCategoryAndAuthorName(category, author);
            } else if (hasTitle) {
                books = bookService.getBooksByTitle(title);
            } else if (hasAuthor) {
                books = bookService.getByAuthorName(author);
            } else if (hasCategory) {
                books = bookService.getByCategory(category);
            } else {
                books = bookService.getAllBooks();
            }
        } catch (AuthorNotFoundException | CategoryNotFoundException ex) {
            log.info(ex.getMessage());
        }

        displayBooks(books);
    }

    private void seeAllAuthors() {
        authorService.findAll()
                .stream()
                .map(Author::toString)
                .forEach(log::info);
    }

    private void seeAllBooks() {
        displayBooks(bookService.getAllBooks());
    }

    private void seeAllBookCategories() {
        categoryService.getAllCategories()
                .stream()
                .map(Category::toString)
                .forEach(log::info);
    }

    /*
    ================================================================================
                                Book Ordering Module(Order management)
    ================================================================================

*/
    private void orderBooksFlow(Scanner scanner) {

        Map<Integer, Integer> cart = new LinkedHashMap<>();
        boolean ordering = true;

        while (ordering) {
            searchBooks(scanner);

            log.info("Enter Book ID to add to order (or 0 to finish): ");
            int bookId = Integer.parseInt(scanner.nextLine());

            if (bookId == 0) {
                ordering = false;
                break;
            }

            log.info("Enter quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            cart.put(bookId, cart.getOrDefault(bookId, 0) + quantity);

            log.info("Book added to order");
            log.info("Add more books? (yes/no): ");
            String choice = scanner.nextLine();

            if (!choice.equalsIgnoreCase("yes")) {
                ordering = false;
            }
        }

        if (!cart.isEmpty()) {
            confirmOrder(cart);
        }
    }

    private void viewMyOrders(Scanner scanner) {

        List<Orders> orders = orderService.getOrdersByUserId(loggedInUser.getUserId());

        if (orders.isEmpty()) {
            log.info("You have no orders");
            return;
        }

        log.info("Your Orders:");
        for (Orders order : orders) {
            log.info("Order ID: {} | Date: {} | Total: {} | Status: {}",
                    order.getOrderId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getStatus());
        }

        log.info("Do you want to view details of a particular order? (yes/no): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("yes")) {
            log.info("Enter Order ID: ");
            int orderId = Integer.parseInt(scanner.nextLine());
            viewAllOrders(orderId);
        }
    }

    private void viewAllOrders(int orderId) {
        List<OrderItem> orderItems = orderService.getOrderItems(orderId);
        if (orderItems.isEmpty()) {
            log.info("No items added in this order");
            return;
        }
        log.info("Details of Order: {}", orderId);
        for (OrderItem orderItem : orderItems) {
            try{
                Book book = bookService.getBookById(orderItem.getBookId());
                log.info("OrderItemID: {} || BookID: {} || Title: {} || Author: {} || Price: {} || Units Ordered: {}",
                        orderItem.getOrderItemId(),
                        orderItem.getBookId(),
                        book.getTitle(),
                        authorService.findByAuthorId(book.getAuthorId()).toString(), // ← Author
                        book.getPrice(),
                        orderItem.getQuantity());
            }catch(BookNotFoundException ex){
                log.info(ex.getMessage());
            }
        }
    }
    private void updateOrderItem(Scanner scanner) {
        log.info("Enter Order ID to view items: ");
        int orderId = Integer.parseInt(scanner.nextLine());

        List<OrderItem> items = orderService.getOrderItems(orderId);

        if (items.isEmpty()) {
            log.info("No items found for Order ID: {}", orderId);
            return;
        }

        // Show all items
        for (OrderItem item : items) {
            log.info("OrderItemID: {} | BookID: {} | Quantity: {} | UnitPrice: {}",
                    item.getOrderItemId(),
                    item.getBookId(),
                    item.getQuantity(),
                    item.getUnitPrice());
        }

        log.info("Enter Order Item ID to update: ");
        int orderItemId = Integer.parseInt(scanner.nextLine());

        log.info("Enter new Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        log.info("Enter new Unit Price: ");
        double unitPrice = Double.parseDouble(scanner.nextLine());

        OrderItem orderItem = orderItemService.getOrderItemById(orderItemId);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);

        try {
            orderItemService.updateOrderItem(orderItem);
            log.info("Order Item updated successfully");
        } catch (OrderItemNotFoundException ex) {
            log.info(ex.getMessage());
        }
    }

    private void deleteOrderItem(Scanner scanner) {
        log.info("Enter Order ID to view items: ");
        int orderId = Integer.parseInt(scanner.nextLine());

        List<OrderItem> items = orderService.getOrderItems(orderId);

        if (items.isEmpty()) {
            log.info("No items found for Order ID: {}", orderId);
            return;
        }

        // Show all items
        for (OrderItem item : items) {
            log.info("OrderItemID: {} | BookID: {} | Quantity: {} | UnitPrice: {}",
                    item.getOrderItemId(),
                    item.getBookId(),
                    item.getQuantity(),
                    item.getUnitPrice());
        }

        log.info("Enter Order Item ID to delete: ");
        int orderItemId = Integer.parseInt(scanner.nextLine());

        try {
            orderItemService.removeOrderItem(orderItemId);
            log.info("Order Item deleted successfully");
        } catch (OrderItemNotFoundException ex) {
            log.info(ex.getMessage());
        }
    }


    private void confirmOrder(Map<Integer, Integer> cart) {

        Orders order = new Orders();
        order.setUserId(loggedInUser.getUserId());
        order.setStatus("PENDING");

        double total = 0;
        List<OrderItem> items = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            try{
                Book book = bookService.getBookById(entry.getKey());
                int quantity = entry.getValue();


                if (book == null || inventoryService.isStockLow(book.getBookId()) || inventoryService.isOutOfStock(book.getBookId(), quantity)) {
                    log.info("ALERT! out of Stock item.");
                    continue;
                }

                OrderItem item = new OrderItem();
                item.setBookId(book.getBookId());
                item.setQuantity(quantity);
                item.setUnitPrice(book.getPrice());

                total += book.getPrice() * quantity;
                items.add(item);
            }catch (BookNotFoundException ex){
                log.info(ex.getMessage());
                continue;
            }
        }

        order.setTotalAmount(total);
        if (total == 0) {
            log.info("No items available to order. All items are out of stock.");
            return; // ← stop here, don't place order
        }
        orderService.placeOrder(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getOrderId());
            //update inventory
            //Add order item
            try {
                orderItemService.addOrderItem(item);
                log.info("Order item added successfully");
            } catch (InvalidOrderItemException |
                     OrderItemNotFoundException ex) {
                log.info(ex.getMessage());
            }
            inventoryService.reduceStock(item.getBookId(), item.getQuantity());

        }

        log.info("Order placed successfully. Order ID: {}", order.getOrderId());
    }

    private void cancelOrder(Scanner scanner){
        viewMyOrders(scanner);
        log.info("Enter order ID to delete: ");
        int orderId = Integer.parseInt(scanner.nextLine());
        try {
            orderService.cancelOrder(orderId);
            log.info("Order cancelled successfully");
        } catch (OrderNotFoundException |
                 InvalidOrderException |
                 InvalidOrderStatusException ex) {
            log.info(ex.getMessage());
        }

    }

    private void reviewBooksFlow(Scanner scanner) {

        boolean reviewing = true;

        while (reviewing) {
            searchBooks(scanner);

            log.info("Enter Book ID to review (or 0 to exit): ");
            int bookId = Integer.parseInt(scanner.nextLine());

            if (bookId == 0) {
                reviewing = false;
                break;
            }

            try{
                Book book = bookService.getBookById(bookId);
                log.info("Selected Book: {}", book.getTitle());
            }catch (BookNotFoundException ex){
                log.info(ex.getMessage());
            }


            int rating;
            while (true) {
                log.info("Enter rating (1 to 5): ");
                rating = Integer.parseInt(scanner.nextLine());
                if (rating >= 1 && rating <= 5) break;
                log.warn("Rating must be between 1 and 5");
            }

            log.info("Enter your review comment: ");
            String comment = scanner.nextLine();

            Review review = new Review();
            review.setUserId(loggedInUser.getUserId());
            review.setBookId(bookId);
            review.setRating(rating);
            review.setComment(comment);

            int res = reviewService.addReview(review);
            if (res < 0) {
                log.info("ERROR.Review not submitted.");
            } else {
                log.info("Review submitted successfully");
            }
            log.info("Do you want to review another book? (yes/no): ");
            String choice = scanner.nextLine();
            if (!choice.equalsIgnoreCase("yes")) {
                reviewing = false;
            }
            log.info("Want to see your reviews (yes/No) ");
            choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("yes")) {
                reviewService.findByUserId(loggedInUser.getUserId())
                        .stream()
                        .forEach(rev -> log.info(rev.toString()));
            }
        }
    }

    /*
    ==================================================================================================
    ==================================================================================================
                                         ADMIN DASHBOARD
    ==================================================================================================
    ==================================================================================================

    */

    private void adminDashboard(Scanner scanner) {

        boolean active = true;

        while (active) {
            log.info("""
                    
                    ===========================
                    ADMIN DASHBOARD
                    ===========================
                    1. Book Management
                    2. Inventory Management
                    3. View All Orders
                    4. View All Reviews
                    5. Logout
                    Enter choice:
                    """);

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                log.warn("Invalid input");
                continue;
            }

            switch (choice) {
                case 1 -> bookManagement(scanner);
                case 2 -> inventoryManagement(scanner);
                case 3 -> viewAllOrders();
                case 4 -> reviewModeration(scanner);
                case 5 -> active = false;
                default -> log.warn("Invalid choice");
            }
        }
    }

    private void bookManagement(Scanner scanner) {

        boolean back = false;

        while (!back) {
            log.info("""
                    
                    ===========================
                    BOOK MANAGEMENT
                    ===========================
                    1. Add Book
                    2. Update Book
                    3. Delete Book
                    4. Back
                    Enter choice:
                    """);

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                log.warn("Invalid input");
                continue;
            }

            switch (choice) {
                case 1 -> addBooks(scanner);
                case 2 -> updateBooks(scanner);
                case 3 -> deleteBooks(scanner);
                case 4 -> back = true;
                default -> log.warn("Invalid choice");
            }
        }
    }

    private void addBooks(Scanner scanner) {

        log.info("Enter book title: ");
        String title = scanner.nextLine();

        log.info("Enter price: ");
        double price = Double.parseDouble(scanner.nextLine());

        log.info("Enter stock quantity: ");
        int stock = Integer.parseInt(scanner.nextLine());

        log.info("Enter author name: ");
        String authorName = scanner.nextLine();

        log.info("Enter category name: ");
        String categoryName = scanner.nextLine();

        Author author = authorService.findByAuthorName(authorName)
                .orElseGet(() -> authorService.addAuthor(authorName));

        Category category = categoryService.findByName(categoryName)
                .orElseGet(() -> categoryService.addCategory(categoryName));

        Book book = new Book();
        book.setTitle(title);
        book.setPrice(price);
        book.setStockQuantity(stock);
        book.setAuthorId(author.getAuthorID());
        book.setCategoryId(category.getCategoryId());

        try{
            bookService.addBook(book);

            log.info("Book added successfully");
        }catch (BookAlreadyExistsException ex){
            log.info(ex.getMessage());
        }
    }

    private void updateBooks(Scanner scanner) {
        searchBooks(scanner);

        log.info("Enter Book ID to update: "); // ← add this
        int bookId = Integer.parseInt(scanner.nextLine());

        log.info("Enter book title: ");
        String title = scanner.nextLine();

        log.info("Enter price: ");
        double price = Double.parseDouble(scanner.nextLine());

        log.info("Enter stock quantity: ");
        int stock = Integer.parseInt(scanner.nextLine());

        log.info("Enter author name: ");
        String authorName = scanner.nextLine();

        log.info("Enter category name: ");
        String categoryName = scanner.nextLine();

        Author author = authorService.findByAuthorName(authorName)
                .orElseGet(() -> authorService.addAuthor(authorName));

        Category category = categoryService.findByName(categoryName)
                .orElseGet(() -> categoryService.addCategory(categoryName));

        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle(title);
        book.setPrice(price);
        book.setStockQuantity(stock);
        book.setAuthorId(author.getAuthorID());
        book.setCategoryId(category.getCategoryId());

        bookService.updateBookWithAllProp(book);

        log.info("Book updated successfully");
    }


    private void deleteBooks(Scanner scanner) {
        searchBooks(scanner);
        log.info("Enter Book ID to delete: ");
        int bookId = Integer.parseInt(scanner.nextLine());

        try{
            int rows = bookService.deleteBook(bookId);
            log.info("Book deleted successfully");
        }catch(BookDeletionException ex){
            log.info(ex.getMessage());
        }
    }

    /*
       ========================================================================================
                                        INVENTORY MANAGEMENT
       ========================================================================================

     */

    private void inventoryManagement(Scanner scanner) {

        boolean back = false;

        while (!back) {
            log.info("""
                    
                    ===========================
                    INVENTORY MANAGEMENT
                    ===========================
                    1. View All Inventory
                    2. Add Inventory
                    3. Update Inventory Quantity
                    4. Delete Inventory Record
                    5. Check Low Stock
                    6. Back
                    Enter choice:
                    """);

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                log.warn("Invalid input");
                continue;
            }

            switch (choice) {
                case 1 -> viewAllInventory();
                case 2 -> addInventory(scanner);
                case 3 -> updateInventoryQuantity(scanner);
                case 4 -> deleteInventory(scanner);
                case 5 -> checkLowStock();
                case 6 -> back = true;
                default -> log.warn("Invalid choice");
            }
        }
    }

    private void viewAllInventory() {
        try{
            inventoryService.getAllInventory()
                    .forEach(inv ->
                            log.info("InventoryID:{} | Book:{} | Quantity:{}",
                                    inv.getInventoryID(),
                                    displayBook(bookService.getBookById(inv.getBookID())),
                                    inv.getQuantity()));
        }catch (BookNotFoundException ex){
            log.info(ex.getMessage());
        }
    }

    private void addInventory(Scanner scanner) {
        seeAllBooks();
        log.info("Enter Book ID: ");
        int bookId = Integer.parseInt(scanner.nextLine());

        log.info("Enter Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        Inventory inventory = new Inventory();
        inventory.setBookID(bookId);
        inventory.setQuantity(quantity);

        try{
            inventoryService.addInventory(inventory);
        }catch (InvalidInventoryDataException ex){
            log.info(ex.getMessage());
        }
    }

    private void updateInventoryQuantity(Scanner scanner) {
        searchBooks(scanner);
        log.info("Enter Book ID: ");
        int bookId = Integer.parseInt(scanner.nextLine());

        log.info("Enter New Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        try{
            inventoryService.updateQuantity(bookId, quantity);
        }catch (InvalidInventoryDataException ex){
            log.info(ex.getMessage());
        }
    }

    private void deleteInventory(Scanner scanner) {
        searchBooks(scanner);
        log.info("Enter Book ID: ");
        int bookId = Integer.parseInt(scanner.nextLine());

        try{
            inventoryService.deleteInventoryBookId(bookId);
        }catch (InventoryNotFoundException ex){
            log.info(ex.getMessage());
        }
    }

    private void checkLowStock() {

        log.info("Following are the books which are out of stock: ");
        List<Book> li = bookService.getAllBooks();
        li.stream().forEach(book -> {
            if (inventoryService.isStockLow(book.getBookId())) {
                log.info("Stock is low for Book ID: {}", book.getBookId());
            }
        });
    }

    private void viewAllOrders() {

        orderService.getAllOrders()
                .forEach(order ->
                        log.info("OrderID:{} | UserID:{} | Total:{} | Status:{}",
                                order.getOrderId(),
                                order.getUserId(),
                                order.getTotalAmount(),
                                order.getStatus()));
    }

    private void reviewModeration(Scanner scanner) {

        List<Review> reviews = reviewService.findAll();

        //dont consider review if have admin in it

        reviews = reviews.stream().filter(review -> !review.getComment().contains("Admin")).collect(Collectors.toList());

        if (reviews.isEmpty()) {
            log.info("No reviews available for moderation");
            return;
        }

        for (Review review : reviews) {

            try{
                Book bookOpt = bookService.getBookById(review.getBookId());


                Book book = bookOpt;

                log.info("""
                                
                                ===========================
                                REVIEW DETAILS
                                ===========================
                                Review ID : {}
                                {}
                                Rating    : {}
                                Comment   : {}
                                """,
                        review.getReviewId(),
                        displayBook(book),
                        review.getRating(),
                        review.getComment()
                );
            }catch (BookNotFoundException ex){
                log.info(ex.getMessage());
            }

            log.info("""
                Choose action:
                1. Moderate (Delete Review)
                2. Skip
                3. Exit Moderation
                Enter choice:
                """);

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                log.warn("Invalid input");
                continue;
            }

            if (choice == 1) {

                log.info("Enter moderation reason:");
                String reason = scanner.nextLine();

                String moderatedComment =
                        "Admin deleted this review. Reason: " + reason;

                int rows = reviewService.moderateReview(
                        review.getReviewId(),
                        moderatedComment
                );

                if (rows > 0) {
                    log.info("Review moderated successfully");
                } else {
                    log.warn("Failed to moderate review");
                }

            } else if (choice == 2) {
                continue;
            } else if (choice == 3) {
                break;
            } else {
                log.warn("Invalid choice");
            }
        }
    }
//
//    private void viewUsers() {
//        List<User> users = userService.getAllUser();
//        users.forEach(user ->
//                log.info("ID: {} | Email: {} | Name: {}",
//                        user.getUserId(), user.getEmail(), user.getName()));
//    }


}