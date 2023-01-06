package vttp2022.paf.assessment.eshop.respositories;

public class Queries {
    
        public static final String SQL_INSERT_CUSTOMER = """
                insert into customer(name, address, email)
                values
                (?, ?, ?)
                """;

        public static final String SQL_SELECT_CUSTOMER_BY_NAME = """
                select name, address, email from customer where name = ?
                """;

        public static final String SQL_AUTHENTICATE_CUSTOMER = """
                select count(*) as auth_state from customer where name = ?
                """;

        public static final String SQL_AUTHENTICATE_CUSTOMER_0 = """
                select * from customer where name = ?
                """;

        public static String SQL_SAVE_ORDER = """
                insert into orders(order_id, delivery_id, name, address, email, status)
                values
                (?, ?, ?, ?, ?, ?)
                """;

        public static String SQL_INSERT_LINE_ITEMS = """
                insert into line_item (item, quantity, orderId)
                values
                (?, ?, ?)
                """;

        public static String SQL_UPDATE_DELIVERY = """
                insert into order_status (order_id, delivery_id, status_id, status_update)
                values
                (?, ?, ?, SYSDATE())
                """;

        public static String SQL_GET_DELIVERY_DETAILS_DISPATCHED = """
                select count(status) as cnt from orders where name = ? and status = "dispatched";
                        """;

        public static String SQL_GET_DELIVERY_DETAILS_PENDING = """
                select count(status) as cnt from orders where name = ? and status = "pending";
                 """;
}
