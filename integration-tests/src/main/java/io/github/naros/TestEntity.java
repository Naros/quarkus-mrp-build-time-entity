package io.github.naros;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * A test entity that addresses two concerns:
 *
 * <ul>
 *     <li>If no target/classes directory is created, the Quarkus maven plugin fails</li>
 *     <li>If no annotated entity mapping is found, the EntityManager dependency is excluded.</li>
 * </ul>
 *
 * @author Chris Cranford
 */
@Entity
public class TestEntity {
    @Id
    @GeneratedValue
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
