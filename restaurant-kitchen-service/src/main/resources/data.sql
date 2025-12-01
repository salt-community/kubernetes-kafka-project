-- Ingredients
INSERT INTO ingredients (id, available_quantity, name)
VALUES
    -- Shared ingredients
    (1, 5, 'Beef Patty'),
    (2, 6, 'Burger Bun'),
    (3, 7, 'Lettuce'), -- Shared with Kebab Rulle
    (4, 6, 'Tomato'),  -- Shared with Kebab Rulle
    (5, 5, 'Cheese Slice'),
    (6, 6, 'Onion'),   -- Shared with Kebab Rulle
    (7, 7, 'Pickles'),
    (8, 6, 'Bacon'),

    -- Pizza ingredients
    (9, 5, 'Pizza Dough'),
    (10, 6, 'Tomato Sauce'),
    (11, 7, 'Mozzarella Cheese'),
    (12, 6, 'Pepperoni'),
    (13, 5, 'Mushrooms'),
    (14, 7, 'Bell Peppers'),
    (15, 6, 'Olives'),

    -- Drink ingredients
    (16, 7, 'Syrup'),
    (17, 6, 'Ice'),
    (18, 5, 'Carbonated Water'),

    -- Kebab Rulle specific ingredients
    (19, 6, 'Flatbread'),
    (20, 7, 'Grilled Meat'),
    (21, 5, 'Cucumber'),
    (22, 6, 'Garlic Sauce'),

    -- Chicken Tikka Masala ingredients
    (23, 6, 'Chicken'),
    (24, 7, 'Tikka Masala Sauce'),
    (25, 5, 'Rice');

-- Recipes
INSERT INTO recipes (id, name, description)
VALUES (1, 'Classic Burger', 'A classic beef burger with fresh ingredients'),
       (2, 'Pepperoni Pizza', 'Traditional pizza with pepperoni and cheese'),
       (3, 'Soda', 'Classic soda'),
       (4, 'Kebab Rulle', 'A delicious wrap with grilled meat and fresh veggies'),
       (5, 'Chicken Tikka Masala', 'Spicy and creamy chicken tikka served with rice');

-- Recipe Ingredients
INSERT INTO recipe_ingredients (recipe_id, ingredient_id)
VALUES
    -- Classic Burger
    (1, 1),  -- Beef Patty
    (1, 2),  -- Burger Bun
    (1, 3),  -- Lettuce
    (1, 4),  -- Tomato
    (1, 5),  -- Cheese Slice
    (1, 6),  -- Onion
    (1, 7),  -- Pickles
    (1, 8),  -- Bacon

    -- Pepperoni Pizza
    (2, 9),  -- Pizza Dough
    (2, 10), -- Tomato Sauce
    (2, 11), -- Mozzarella Cheese
    (2, 12), -- Pepperoni
    (2, 13), -- Mushrooms
    (2, 14), -- Bell Peppers
    (2, 15), -- Olives

    -- Soda
    (3, 16), -- Syrup
    (3, 17), -- Ice
    (3, 18), -- Carbonated Water

    -- Kebab Rulle (using shared ingredients for lettuce, tomato, onion)
    (4, 19), -- Flatbread
    (4, 20), -- Grilled Meat
    (4, 3),  -- Lettuce (shared)
    (4, 4),  -- Tomato (shared)
    (4, 6),  -- Onion (shared)
    (4, 21), -- Cucumber
    (4, 22), -- Garlic Sauce

    -- Chicken Tikka Masala
    (5, 23), -- Chicken
    (5, 24), -- Tikka Masala Sauce
    (5, 25); -- Rice