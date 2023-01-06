package vttp2022.paf.assessment.eshop.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpSession;
import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.respositories.CustomerRepository;
import vttp2022.paf.assessment.eshop.respositories.OrderRepository;
import vttp2022.paf.assessment.eshop.services.WarehouseService;

@RestController
@RequestMapping(path="/order", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

	@Autowired
	private CustomerRepository custRepo;

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private WarehouseService warehousSvc;

	private static final String DISPATCHED = "dispatched";
	// task 3
	@PostMapping
	public ResponseEntity<String> postOrder(@RequestBody MultiValueMap<String, String> form, HttpSession sess) {

		System.out.println(form.getFirst("name"));
		String name = form.getFirst("name");

		// Step a: Query to see if customer exists
		Optional<Customer> opt = custRepo.findCustomerByName(name);

		JsonObjectBuilder bld = Json.createObjectBuilder();

		if (opt.isEmpty()) {
			bld.add("error", "Customer <%s> not found".formatted(name));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(bld.build().toString());
		}

		Customer c = opt.get();

		// Step b: input order details
		
		String order_Id = UUID.randomUUID().toString().substring(0,8);
		System.out.println(order_Id);

		Order o = new Order();
		o.setOrderId(order_Id);
		o.setCustomer(c);

		String item_name = form.getFirst("item");
		Integer quantity = Integer.parseInt(form.getFirst("quantity"));

		List<LineItem> items = new LinkedList<>();
		LineItem li = new LineItem();
		li.setItem(item_name);
		li.setQuantity(quantity);
		System.out.println(item_name);

		o.setLineItems(items);

		// Step d: save orders into database and return ResponseEntity for successful order
		try {
		orderRepo.saveOrder(o);
		} catch (Exception ex) {
			bld.add("error", ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(bld.toString());
		}

		JsonArrayBuilder liBld = Json.createArrayBuilder();
		liBld.add(Json.createObjectBuilder()
			.add("item", item_name)
			.add("quantity", quantity).build());

		bld.add("orderId", order_Id)
			.add("name", name)
			.add("address", c.getAddress())
			.add("email", c.getEmail())
			.add("lineItems", liBld)
			.build();

		return ResponseEntity.ok().body(bld.toString());
	}


	// Task 4
	@PostMapping("/dispatched")
	public ResponseEntity<String> dispatchOrder(@RequestBody MultiValueMap<String, String> form) {
		System.out.println(form.getFirst("name"));
		String name = form.getFirst("name");

		// Step a: Query to see if customer exists
		Optional<Customer> opt = custRepo.findCustomerByName(name);

		JsonObjectBuilder bld = Json.createObjectBuilder();

		if (opt.isEmpty()) {
			bld.add("error", "Customer <%s> not found".formatted(name));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(bld.build().toString());
		}

		Customer c = opt.get();

		// Step b: input order details
		
		String order_Id = UUID.randomUUID().toString().substring(0,8);
		System.out.println(order_Id);

		Order o = new Order();
		o.setOrderId(order_Id);
		o.setCustomer(c);

		String item_name = form.getFirst("item");
		Integer quantity = Integer.parseInt(form.getFirst("quantity"));

		List<LineItem> items = new LinkedList<>();
		LineItem li = new LineItem();
		li.setItem(item_name);
		li.setQuantity(quantity);
		System.out.println(item_name);

		o.setLineItems(items);

		try {
			orderRepo.saveOrder(o);
			} catch (Exception ex) {
				bld.add("error", ex.getMessage());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(bld.toString());
			}

		OrderStatus os = warehousSvc.dispatch(o);

		// Task 5
		if (os.getStatus()!=DISPATCHED) {
			bld.add("orderId", os.getOrderId())
				.add("status", "pending")
				.build();
		} else {
			bld.add("orderId", os.getOrderId())
				.add("deliveryId", os.getDeliveryId())
				.add("status", "dispatched")
				.build();
		}
		return ResponseEntity.ok(bld.toString());

	}

	@GetMapping("/api/order/{name}/status")
	public ResponseEntity<String> getStatus(@PathVariable String name) {
		Integer pending = orderRepo.getPending(name);
		Integer dispatched = orderRepo.getDispatched(name);

		JsonObjectBuilder bld = Json.createObjectBuilder();
		bld.add("name", name)
			.add("dispatched", dispatched)
			.add("pending", pending);
		
			
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(bld.build().toString());
	}
}
