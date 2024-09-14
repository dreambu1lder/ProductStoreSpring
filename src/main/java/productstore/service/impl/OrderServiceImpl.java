package productstore.service.impl;

import productstore.dao.OrderDao;
import productstore.dao.impl.OrderDaoImpl;
import productstore.service.OrderService;

public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao = new OrderDaoImpl();
}
