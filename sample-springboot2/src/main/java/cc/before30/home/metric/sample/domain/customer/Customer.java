package cc.before30.home.metric.sample.domain.customer;

import cc.before30.home.metric.sample.util.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="sample_customer")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id")
    private Long id;

    @Column(name = "u_first")
    private String first;

    @Column(name = "u_last")
    private String last;
}
