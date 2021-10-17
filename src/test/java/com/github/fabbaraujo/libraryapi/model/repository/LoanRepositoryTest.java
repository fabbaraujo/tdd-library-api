package com.github.fabbaraujo.libraryapi.model.repository;

import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class LoanRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LoanRepository repository;

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro.")
    void existsByBookAndNotReturnedTest() {
        Loan loan = createAndPersistLoan(LocalDate.now());
        Book book = loan.getBook();

        boolean exists = repository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer")
    void findByBookIsbnOrCustomerTest() {
        createAndPersistLoan(LocalDate.now());

        Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Fulano", PageRequest.of(0, 10));

        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isZero();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve retornar vazio quando não houver empréstimos atrasados.")
    void notFindByLoanDateLessThenAndNotReturnedTest() {
        Loan loan = createAndPersistLoan(LocalDate.now());

        List<Loan> result = repository.findByLoanDateLessThenAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Deve obter empréstimos cuja data empréstimo for menor ou igual a três dias atrás e não retornados.")
    void findByLoanDateLessThenAndNotReturnedTest() {
        Loan loan = createAndPersistLoan(LocalDate.now().minusDays(5));

        List<Loan> result = repository.findByLoanDateLessThenAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(loan);
    }

    private Loan createAndPersistLoan(LocalDate loanDate) {
        Book book = Book.builder()
                .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build();
        entityManager.persist(book);
        Loan loan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(loanDate)
                .build();
        entityManager.persist(loan);
        return loan;
    }
}
