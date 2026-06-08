package com.test.aibackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 부서명
     * 예: 개발팀, 기획팀, 마케팅팀
     */
    @Column(nullable = false)
    private String name;

    /**
     * 부서가 소속된 회사
     *
     * Department N : 1 Company
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * 부서에 소속된 직원 목록
     *
     * Department 1 : N Employee
     *
     * mappedBy = "department"
     * - Employee 엔티티의 department 필드가 연관관계의 주인이라는 뜻입니다.
     * - 실제 FK는 employees 테이블의 department_id 컬럼에 있습니다.
     *
     * 실무에서는 기본적으로 LAZY를 유지합니다.
     * 직원 목록이 필요한 조회 API에서만 fetch join으로 함께 가져옵니다.
     */
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    /**
     * 부서명 수정
     */
    public void updateName(String name) {
        this.name = name;
    }
}