package cc.before30.home.metric.sample.domain.customer;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(final CustomerRepository customerRepository) {
        this.repository = customerRepository;
    }

    public Iterable<Customer> findAll() {
        return repository.findAll();
    }

    public Optional<Customer> findOne(Long id) {
        return repository.findById(id);
    }

    public Customer saveOne(Customer customer) {
        return repository.save(customer);
    }
}
