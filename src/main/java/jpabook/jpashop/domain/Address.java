package jpabook.jpashop.domain;


import lombok.Getter;

import javax.persistence.Embeddable;


//값타입은 변경불가능하게 생성해야하므로 @Setter안씀
@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    //스펙상 필수로 선언해야함.기본 생성자 제약두는 이유는
    //JPA 구현라이브러리가 객체 생성시 리플렉션 같은 기술 사용해야하므로.
    protected Address() {}

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
