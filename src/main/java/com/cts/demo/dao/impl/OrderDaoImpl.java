package com.cts.demo.dao.impl;

import com.cts.demo.dao.OrderDao;
import com.cts.demo.model.OrderItem;
import com.cts.demo.model.Orders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * JDBC-based implementation of {@link OrderDao}.
 */
@Repository
@Slf4j
public class OrderDaoImpl implements OrderDao {

    private static final String INSERT_SQL = "INSERT INTO Orders (UserID, TotalAmount, Status) VALUES (?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM Orders WHERE OrderID = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM Orders WHERE UserID = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM Orders";
    private static final String UPDATE_STATUS_SQL = "UPDATE Orders SET Status = ? WHERE OrderID = ?";
    private static final String DELETE_SQL = "DELETE FROM Orders WHERE OrderID = ?";
    private static final String FIND_ORDER_ITEMS_SQL = """
            SELECT * FROM ORDERS O JOIN ORDERITEM OI ON O.ORDERID=OI.ORDERID
            WHERE O.ORDERID=?
            """;
    private static final String UPDATE_ORDER_SQL = """
            UPDATE ORDERS SET STATUS=? WHERE ORDERID=?
            """;

    private final JdbcTemplate jdbcTemplate;

    public OrderDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OrderItem> orderItemRowMapper = (rs, rowNum) ->
            new OrderItem(
                    rs.getInt("OrderItemID"),
                    rs.getInt("OrderID"),
                    rs.getInt("BookID"),
                    rs.getInt("Quantity"),
                    rs.getDouble("UnitPrice")
            );

    private final RowMapper<Orders> orderRowMapper = (rs, rowNum) -> {
        Orders order = new Orders();
        order.setOrderId(rs.getInt("OrderID"));
        order.setUserId(rs.getInt("UserID"));
        order.setOrderDate(rs.getTimestamp("OrderDate"));
        order.setTotalAmount(rs.getDouble("TotalAmount"));
        order.setStatus(rs.getString("Status"));
        return order;
    };

    /**
     * Places a new order and sets the generated order ID back on the order object.
     *
     * @param order
     */
    @Override
    public void placeOrder(Orders order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, order.getUserId());
            ps.setDouble(2, order.getTotalAmount());
            ps.setString(3, order.getStatus());
            return ps;
        }, keyHolder);

        order.setOrderId(keyHolder.getKey().intValue());
        log.info("Order placed successfully! OrderID: {}", order.getOrderId());
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId
     * @return the matching order, or null if not found
     */
    @Override
    public Orders getOrderById(int orderId) {
        List<Orders> result = jdbcTemplate.query(FIND_BY_ID_SQL, orderRowMapper, orderId);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Retrieves all orders placed by a specific user.
     *
     * @param userId
     * @return a list of orders belonging to the user
     */
    @Override
    public List<Orders> getOrdersByUserId(int userId) {
        return jdbcTemplate.query(FIND_BY_USER_ID_SQL, orderRowMapper, userId);
    }

    /**
     * Retrieves all orders from the database.
     *
     * @return a list of all orders
     */
    @Override
    public List<Orders> getAllOrders() {
        return jdbcTemplate.query(FIND_ALL_SQL, orderRowMapper);
    }

    /**
     * Updates the status of an existing order.
     *
     * @param orderId
     * @param status
     */
    @Override
    public void updateOrderStatus(int orderId, String status) {
        int rows = jdbcTemplate.update(UPDATE_STATUS_SQL, status, orderId);
        if (rows > 0) {
            log.info("Order status updated to: {}", status);
        } else {
            log.warn("Order not found with ID: {}", orderId);
        }
    }

    /**
     * Deletes an order by its ID.
     *
     * @param orderId
     */
    @Override
    public void deleteOrder(int orderId) {
        int rows = jdbcTemplate.update(DELETE_SQL, orderId);
        if (rows > 0) {
            log.info("Order deleted. ID: {}", orderId);
        } else {
            log.warn("Order not found with ID: {}", orderId);
        }
    }

    /**
     * Retrieves all order items belonging to a specific order.
     *
     * @param orderId
     * @return a list of order items
     */
    @Override
    public List<OrderItem> getOrderItems(int orderId) {
        return jdbcTemplate.query(FIND_ORDER_ITEMS_SQL, orderItemRowMapper, orderId);
    }

    /**
     * Updates the full order record.
     *
     * @param order
     * @return true if the update was successful, false otherwise
     */
    @Override
    public boolean updateOrder(Orders order) {
        return Boolean.TRUE.equals(jdbcTemplate.update(UPDATE_ORDER_SQL, Boolean.class, order.getStatus(), order.getOrderId()));
    }
}