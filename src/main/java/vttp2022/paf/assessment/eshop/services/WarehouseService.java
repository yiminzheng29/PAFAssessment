package vttp2022.paf.assessment.eshop.services;

import java.io.StringReader;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.respositories.OrderRepository;

@Service
public class WarehouseService {


	private static final String endpt = "http://paf.chuklee.com/dispatch";

	@Autowired
	private OrderRepository orderRepo;

	public OrderStatus dispatch(Order order) {

		List<LineItem> li = order.getLineItems();

		String payload;

		JsonObjectBuilder libld = Json.createObjectBuilder();

		for (LineItem i : li) {
			libld.add("item", i.getItem());
			libld.add("quantity", i.getQuantity());
		}

		JsonArrayBuilder arrBld = Json.createArrayBuilder();
		arrBld.add(libld).build();

		JsonObject json = Json.createObjectBuilder()
			.add("orderId", order.getOrderId())
			.add("name", order.getName())
			.add("address", order.getAddress())
			.add("email", order.getEmail())
			.add("lineItems", arrBld)	
			.add("createdBy", "Zheng Yimin")
			.build();


			String url = UriComponentsBuilder.fromUriString(endpt)
				.queryParam("order_Id", order.getOrderId())
				.toUriString();

			RequestEntity<String> req = RequestEntity
				.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				// .headers("Accept", MediaType.APPLICATION_JSON)
				.body(json.toString(), String.class);
			RestTemplate temp = new RestTemplate();
			ResponseEntity<String> resp = temp.exchange(req, String.class);
			payload = resp.getBody();
			

		
		JsonReader jr = Json.createReader(new StringReader(payload));
        JsonObject jo = jr.readObject();
		
		OrderStatus os = new OrderStatus();
		os.setOrderId(order.getOrderId());

		if (payload.contains("deliveryId")) {
			os.setStatus("dispatched");
			os.setDeliveryId(jo.getString("deliveryId"));
		}
		return os;

	}
}
