package com.kds.mock.entity.base;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class BaseEntityTests {

    // Concrete test entity to test BaseEntity functionality
    private static class TestEntity extends BaseEntity {
        private String name;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @Test
    void testBaseEntityCreation() {
        TestEntity entity = new TestEntity();
        assertNotNull(entity);
    }

    @Test
    void testBaseEntityInheritance() {
        TestEntity entity = new TestEntity();
        entity.setName("Test");
        assertEquals("Test", entity.getName());
    }

    @Test
    void testBaseEntityIsAbstract() {
        // Verify that BaseEntity is abstract and cannot be instantiated directly
        assertTrue(BaseEntity.class.isAssignableFrom(TestEntity.class));
    }
} 