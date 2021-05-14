package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MemberRepository extends JpaRepository<Member,Long> {

    //실행하면 findByName 되어있으면 타입들보고
    //select m from Member m where m.name = ?
    //name중요!
    List<Member> findByName(String name);
}
