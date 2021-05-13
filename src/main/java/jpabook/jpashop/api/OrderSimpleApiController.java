package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/*
    xToOne[ManyToOne, OneToOne]
    Order
    Order -> Member
    Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;
    /*
    엔티티 직접노출하는 방법 쓰면안됨!
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        //양방향탐색하므로 무한루프 걸림
        //JsonIgnore 써야됨
        //근데 또 에러 발생.
        //Order를 가지고 왔음 근데 오더의 멤버는 LAZY.
        //지연로딩이므로 진짜멤버객체를 안가지고오고 오더의 데이터만 가져옴.
        // 프록시 객체를 가짜로 넣어놓고 뭔가 멤버를 찾으면 그때 db를 들려서 프록시초기화.
        //문제는 루프를 돌리는데 Order를 가지고 멤버를 뽑아보려 하는데 ByteBuddy 즉
        //프록시를 처리할 수 가 없음. 그래서 어떻게 해결해야되냐? 지연로딩인 경우에는 그냥
        // JSON라이브러리야 얘를 뿌리지마. 하이버네이트5 모듈을 설치해야함.
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //실제네임끌고 오므로 Lazy강제 초기화
            order.getDelivery().getAddress();// Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2(){
        //ORDER 2개 조회
        //N+1 -> 1 + 회원 N + 배송 N
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result=orders.stream()
                .map(o-> new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4(){
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static private class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }
    }
}











