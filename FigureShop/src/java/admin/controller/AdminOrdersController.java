package admin.controller;

import constants.Message;
import constants.Router;
import constants.StatusCode;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import order.daos.OrderDao;
import utils.GetParam;
import order.models.Order;
import utils.Helper;

/**
 *
 * @author locnh
 */
@WebServlet(name = "ViewOrderController", urlPatterns = {"/" + Router.ADMIN_ORDERS_CONTROLLER})
public class AdminOrdersController extends HttpServlet {

    protected boolean processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/html;charset=UTF-8");
        OrderDao orderDao = new OrderDao();

        // get param
        String fromDate = GetParam.getStringParam(request, "fromDate", "from date", 7, 12, null);
        String toDate = GetParam.getStringParam(request, "toDate", "to date", 7, 12, null);
        Integer page = GetParam.getIntParams(request, "page", "Page", 1, Integer.MAX_VALUE, 1);

        if (fromDate == null) {
            fromDate = "2000-1-1";
        }

        if (toDate == null) {
            toDate = "2077-1-1";
        }

        // get orders
        ArrayList<Order> orders = orderDao.getOrdersByDate(fromDate, toDate, page);
        ArrayList<Order> allOrders = orderDao.getOrders();

        Integer maxPage = allOrders.size() / 20;
        if (allOrders.size() % 20 != 0) {
            maxPage++;
        }

        request.setAttribute("orders", orders);
        request.setAttribute("maxPage", maxPage);
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (!processRequest(request, response)) {
                // forward on 404
                Helper.setAttribute(request, StatusCode.NOT_FOUND.getValue(),
                        Message.NOT_FOUND_ERROR_TITLE.getContent(),
                        Message.NOT_FOUND_ERROR_MESSAGE.getContent());
                request.getRequestDispatcher(Router.ERROR).forward(request, response);
                return;
            }
            // forward on 200
            request.getRequestDispatcher(Router.ADMIN_ORDERS_PAGE).forward(request, response);
        } catch (Exception e) {
            System.out.println(e);
            // forward on 500
            Helper.setAttribute(request, StatusCode.INTERNAL_SERVER_ERROR.getValue(),
                    Message.INTERNAL_ERROR_TITLE.getContent(),
                    Message.INTERNAL_ERROR_MESSAGE.getContent());
            request.getRequestDispatcher(Router.ERROR).forward(request, response);
        }
    }

}
