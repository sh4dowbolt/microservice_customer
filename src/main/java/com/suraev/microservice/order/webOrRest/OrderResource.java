package com.suraev.microservice.order.webOrRest;

import com.suraev.microservice.order.domain.Order;
import com.suraev.microservice.order.repository.OrderRepository;
import com.suraev.microservice.order.service.OrderService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.apache.tomcat.util.http.HeaderUtil;
import org.apache.tomcat.util.http.ResponseUtil;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class OrderResource {
    private final Logger log = LoggerFactory.getLogger(OrderResource.class);
    private static final String ENTITY_NAME = "order";

    @Value("${spring.application.name}")
    private String applicationName;

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderResource(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }


    /**
    {@Code POST /orders} : Создать новый заказ
     * @param order - это заказ, который необходимо создать
     * @return возвращает {@link ResponseEntity} со статусом {@code 201 (Created)} с телом нового заказа,или со статусом {@code 400 (Bad Request)} если у закажа уже есть ID.
     * @throws ResponseStatusException если пытаемся добавить заказ с ID.
      */

    @PostMapping("/orders")
    @Transactional
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to save Order: {}", order);

        if(order.getId()!=null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A new order cannot already have an ID");
        }

        final var result = orderRepository.save(order);
        orderService.createOrder(result);

        HttpHeaders headers= new HttpHeaders();
        String message = String.format("A new %s is created with identifier %s", ENTITY_NAME, result.getId().toString());
        headers.add("X-" + applicationName + "-alert", message);
        headers.add("X-" + applicationName + "-params", result.getId().toString());

        return ResponseEntity.created(new URI("/api/orders/" + result.getId())).headers(headers).body(result);
    }

    /**
     {@Code PUT /orders} : Обновление существующего заказа
     * @param order - для обновления существующего заказа
     * @return {@link ResponseEntity} со статусом {@code 200 (OK)} с телом обновленного заказа,
     * или со статусом {@code 400 (Bad Request)} если хотим обновить несуществующий заказ,
     * или со статусом {@code 500 (Internal Server Error)} если заказ не может быть обновлен.
     * @throws  ResponseStatusException если пытаемся обновить заказ с несуществующим ID.
     */

    @PutMapping("/orders")
    @Transactional
    public ResponseEntity<Order> update(@Valid @RequestBody Order order) {
        log.debug("REST request to update Order: {}", order);
        if(order.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ENTITY_NAME+"id is null");
        }
        final var result = orderRepository.save(order);
        orderService.updateOrder(result);

        HttpHeaders headers= new HttpHeaders();
        String message = String.format("A %s is updated with identifier %s", ENTITY_NAME, result.getId().toString());
        headers.add("X-" + applicationName + "-alert", message);
        headers.add("X-" + applicationName + "-params", result.getId().toString());

        return ResponseEntity.ok().headers(headers).body(result);
    }


    /**
     * {@Code GET  /orders} : получить все заказы
     * @return {@link ResponseEntity} со статусом {@code 200 (OK)} и лист заказов теле запроса.
     */
    @GetMapping("/orders")
    @Transactional
    public List<Order> getAllOrders() {
        log.debug("REST request to get all Orders");
        return orderRepository.findAll();
    }


    /**
     * {@Code GET /orders/{id}} : получить заказ по ID
     * @param id это ID извлекаемого заказа
     * @throws Exception генерируется исключение при некорректном ID
     * @return {@link ResponseEntity} со статусом {@Code 200 (OK)}  с телом заказа, или со статусом {@Code 404 (Not found)}
     */
    @GetMapping("/orders/{id}")
    @Transactional
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        log.debug("REST request to get Order : {}", id);

        try {
            final var result = orderRepository.findById(id).orElseThrow();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * {@Code DELETE /orders/{id}} : удалить заказ по ID
     * @param id это ID удаляемого заказа
     * @return {@link ResponseEntity} со статусом {@Code 204 (NO_CONTENT)}
     */

    @DeleteMapping("/orders/{id}")
    @Transactional
    public ResponseEntity <Void> deleteOrder(@PathVariable String id) {
        log.debug("REST request to delete Order: {}", id);

        final var orderOptional = orderRepository.findById(id);
        orderRepository.deleteById(id);

        if(orderOptional.isPresent()) {
            orderService.deleteOrder(orderOptional.get());
        }

        HttpHeaders headers= new HttpHeaders();
        String message = String.format("A %s is deleted with identifier %s", ENTITY_NAME, id);
        headers.add(applicationName, message);


        return ResponseEntity.noContent().headers(headers).build();





    }





}
