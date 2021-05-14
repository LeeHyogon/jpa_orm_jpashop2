package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    /*
      *주문
     */
    @Transactional
    public Long order(Long memberId,Long itemId,int count){
        //엔티티 조회
        Member member=memberRepository.findById(memberId).get();
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery= new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem=OrderItem.createOrderItem(item,item.getPrice(),count);

        //주문 생성
        Order order =Order.createOrder(member,delivery,orderItem);

        //주문 저장
        // casecade의 범위는 어디까지하는게좋을까
       //딜리버리랑 오더아이템은 오더만 참조해서 씀.
        //딜리버리가 매우중요해서 다른곳에서다 가져다쓰면 cascade함부로쓰면안됨.
        orderRepository.save(order);

        return order.getId();
    }

    /*
    주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        //주문 엔티티 조회
        Order order=orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByString(orderSearch);
    }

}
