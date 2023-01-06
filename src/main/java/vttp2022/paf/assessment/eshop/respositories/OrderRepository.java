package vttp2022.paf.assessment.eshop.respositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;

import static vttp2022.paf.assessment.eshop.respositories.Queries.*;

import java.util.List;

@Repository
public class OrderRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional(rollbackFor = OrderException.class)
	public boolean saveOrder(Order o) {
		
		return jdbcTemplate.update(SQL_SAVE_ORDER, 
			o.getOrderId(), o.getDeliveryId(), o.getName(), o.getAddress(), o.getEmail(),
			o.getOrderDate()) > 0;
	}

	public void addLineItems(List<LineItem> items, String orderId) {
		List<Object[]> data = items.stream()
			.map(li -> {
				Object[] l = new Object[3];
				l[0] = li.getItem();
				l[1] = li.getQuantity();
				l[2] = orderId;
				return l;
			}).toList();

		jdbcTemplate.batchUpdate(SQL_INSERT_LINE_ITEMS, data);
	}

	public boolean updateDelivery(Order os) {
		return jdbcTemplate.update(SQL_UPDATE_DELIVERY, os.getOrderId(), os.getDeliveryId(), os.getStatus()) > 0;
	}

	public Integer getPending(String name) {
		Integer pending = 0;
		SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_DELIVERY_DETAILS_PENDING, name);
		while (rs.next()) {
			pending = rs.getInt("cnt");
		}


		return pending;

	}

	public Integer getDispatched(String name) {
		// for getting dispatched orders
		Integer dispatched = 0;
		SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_DELIVERY_DETAILS_DISPATCHED, name);
		while (rs.next()) {
			dispatched = rs.getInt("cnt");
		}


		return dispatched;

	}
}
