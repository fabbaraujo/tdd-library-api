package com.github.fabbaraujo.libraryapi.model.repository;

import com.github.fabbaraujo.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
