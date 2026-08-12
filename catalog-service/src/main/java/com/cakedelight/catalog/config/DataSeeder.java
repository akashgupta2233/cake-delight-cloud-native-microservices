package com.cakedelight.catalog.config;

import com.cakedelight.catalog.entity.Cake;
import com.cakedelight.catalog.repository.CakeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the database with sample cake data on application startup.
 * Runs only if the database is empty.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CakeRepository cakeRepository;

    public DataSeeder(CakeRepository cakeRepository) {
        this.cakeRepository = cakeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (cakeRepository.count() > 0) {
            return;
        }

        List<String> urls = List.of(
                "https://images.unsplash.com/photo-1562440499-64c9a111f713?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://plus.unsplash.com/premium_photo-1713447395823-2e0b40b75a89?q=80&w=682&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62?q=80&w=803&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1618426703623-c1b335803e07?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1559620192-032c4bc4674e?q=80&w=729&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1616690710400-a16d146927c5?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1569289522127-c0452f372d46?q=80&w=681&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1623428454614-abaf00244e52?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1565661834013-d196ca46e14e?q=80&w=688&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1559553156-2e97137af16f?q=80&w=733&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1581745069539-1e60d7f965f4?q=80&w=688&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1516054575922-f0b8eeadec1a?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1488477304112-4944851de03d?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1560180474-e8563fd75bab?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1552689486-ce080445fbb6?q=80&w=731&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1549572189-dddb1adf739b?q=80&w=691&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1519915028121-7d3463d20b13?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://plus.unsplash.com/premium_photo-1663839331018-e11b06c3ac35?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1576956555607-18c2df9a47f6?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://plus.unsplash.com/premium_photo-1714342967585-fc96cb3db818?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        );

        List<String> names = List.of(
                "Macarons Cake",
                "Raspberry Layer Cake",
                "Black Forest Cake",
                "Blueberry Walnut Spice Cake",
                "Blackberry Lilac Cake",
                "Oreo Drip Cake",
                "Pink Cherry Layer Cake",
                "Floral Berry Tiered Wedding Cake",
                "Semi-Naked Fig and Pistachio Cake",
                "Chocolate Berry Birthday Cake",
                "Lavender Floral Tiered Wedding Cake",
                "Olive Oil Cake",
                "Mango Blood Orange Layer Cake",
                "Orange and Blackberry Mini Tarts",
                "Lucky Charms Rainbow Drip Cake",
                "Chocolate Ferrero Rocher Drip Cake",
                "Lemon Meringue Tart",
                "Funfetti Birthday Cake",
                "Gingerbread House Cake",
                "Naked Strawberry Red Velvet Cake"
        );

        List<Cake> cakes = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            String desc = "Delicious " + name + ".";
            String category = (i % 2 == 0) ? "Celebration" : "Classic";
            BigDecimal price = BigDecimal.valueOf(250 + (i * 50));
            String imageUrl = (i < urls.size()) ? urls.get(i) : null;

            Cake c = Cake.builder()
                    .name(name)
                    .description(desc)
                    .category(category)
                    .price(price)
                    .availability(true)
                    .imageUrl(imageUrl)
                    .build();
            cakes.add(c);
        }

        cakeRepository.saveAll(cakes);
        System.out.println("Seeded " + cakes.size() + " cakes.");
    }
}
