package com.test.aibackend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 직원 이름
     * 예: 홍길동, 김철수, 이영희
     */
    @Column(nullable = false)
    private String name;

    /**
     * 직급
     * 예: 사원, 대리, 과장
     */
    @Column(nullable = false)
    private String position;

    /**
     * 직원이 소속된 회사
     *
     * Employee N : 1 Company
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /**
     * 직원이 배치된 부서
     *
     * Employee N : 1 Department
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /**
     * 직원 기본 정보 수정
     */
    public void updateInfo(String name, String position) {
        this.name = name;
        this.position = position;
    }

    /**
     * 직원 부서 이동
     */
    public void changeDepartment(Department department) {
        this.department = department;
    }
}