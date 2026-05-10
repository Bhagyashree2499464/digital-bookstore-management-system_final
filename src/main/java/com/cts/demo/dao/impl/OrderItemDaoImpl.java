package com.cts.demo.dao.impl;

import com.cts.demo.dao.OrderItemDao;
import com.cts.demo.model.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * JDBC-based implementation of {@link OrderItemDao}.
 */
@Repository
@Slf4j
public class OrderItemDaoImpl implements OrderItemDao {

    private static final String INSERT_SQL = "INSERT INTO OrderItem (OrderID, BookID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM OrderItem WHERE OrderItemID = ?";
    private static final String FIND_BY_ORDER_ID_SQL = "SELECT * FROM OrderItem WHERE OrderID = ?";
    private static final String UPDATE_SQL = "UPDATE OrderItem SET BookID = ?, Quantity = ?, UnitPrice = ? WHERE OrderItemID = ?";
    private static final String DELETE_SQL = "DELETE FROM OrderItem WHERE OrderItemID = ?";

    private JdbcTemplate jdbcTemplate;

    public OrderItemDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper using Lambda
    private final RowMapper<OrderItem> rowMapper = (rs, rowNum) -> new OrderItem(
            rs.getInt("OrderItemID"),
            rs.getInt("OrderID"),
            rs.getInt("BookID"),
            rs.getInt("Quantity"),
            rs.getDouble("UnitPrice")
    );

    /**
     * Adds a new order item and sets the generated ID back on the order item object.
     *
     * @param orderItem
     */
    @Override
    public void addOrderItem(OrderItem orderItem) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, orderItem.getOrderId());
            ps.setInt(2, orderItem.getBookId());
            ps.setInt(3, orderItem.getQuantity());
            ps.setDouble(4, orderItem.getUnitPrice());
            return ps;
        }, keyHolder);

        orderItem.setOrderItemId(keyHolder.getKey().intValue());
        log.info("OrderItem added! ID: {}", orderItem.getOrderItemId());
    }

    /**
     * Retrieves an order item by its ID.
     *
     * @param orderItemId
     * @return the matching order item, or null if not found
     */
    @Override
    public OrderItem getOrderItemById(int orderItemId) {
        List<OrderItem> result = jdbcTemplate.query(FIND_BY_ID_SQL, rowMapper, orderItemId);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Retrieves all order items belonging to a specific order.
     *
     * @param orderId
     * @return a list of order items
     */
    @Override
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        return jdbcTemplate.query(FIND_BY_ORDER_ID_SQL, rowMapper, orderId);
    }

    /**
     * Updates an existing order item.
     *
     * @param orderItem
     */
    @Override
    public void updateOrderItem(OrderItem orderItem) {
        int rows = jdbcTemplate.update(UPDATE_SQL,
                orderItem.getBookId(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getOrderItemId());

        if (rows > 0) {
            log.info("OrderItem updated. ID: {}", orderItem.getOrderItemId());
        } else {
            log.warn("OrderItem not found. ID: {}", orderItem.getOrderItemId());
        }
    }

    /**
     * Deletes an order item by its ID.
     *
     * @param orderItemId
     */
    @Override
    public void deleteOrderItem(int orderItemId) {
        int rows = jdbcTemplate.update(DELETE_SQL, orderItemId);
        if (rows > 0) {
            log.info("OrderItem deleted. ID: {}", orderItemId);
        } else {
            log.warn("OrderItem not found. ID: {}", orderItemId);
        }
    }
}