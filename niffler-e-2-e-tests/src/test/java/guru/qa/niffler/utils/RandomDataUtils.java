package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

public class RandomDataUtils {

    private static final Faker faker = new Faker();

    public static String randomUsername() {
        return faker.name().username();
    }

    public static String randomCategoryName() {
        return faker.lorem().word();
    }

    public static String randomSentence(int wordsCount) {
        return faker.lorem().sentence(wordsCount);
    }

    public static int randomAmount(int min, int max) {
        return faker.number().numberBetween(min, max);
    }
}
