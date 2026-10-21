package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.CalculateRequest;
import guru.qa.niffler.grpc.CalculateResponse;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.grpc.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CurrencyGrpcTests extends BaseCurrencyGrpcTest {

    @Test
    void allCurrenciesShouldReturned() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();
        Assertions.assertEquals(4, allCurrenciesList.size());
    }

    @Test
    void calculateRateShouldReturnCorrectValue() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.USD)
                .setAmount(100.0)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);
        double calculatedAmount = response.getCalculatedAmount();
        Assertions.assertTrue(calculatedAmount > 0);
    }

    @Test
    void allCurrenciesShouldHavePositiveRate() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();

        for (Currency currency : allCurrenciesList) {
            Assertions.assertTrue(currency.getCurrencyRate() > 0);
        }
    }

    @Test
    void sameCurrencyConversionShouldReturnSameAmount() {
        double amount = 150.0;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.RUB)
                .setAmount(amount)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);

        Assertions.assertEquals(amount, response.getCalculatedAmount());
    }

    @Test
    void zeroAmountConversionShouldReturnZero() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.USD)
                .setAmount(0.0)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);

        Assertions.assertEquals(0.0, response.getCalculatedAmount());
    }

    @Test
    void negativeAmountConversionShouldReturnNegative() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.USD)
                .setAmount(-50.0)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);

        Assertions.assertTrue(response.getCalculatedAmount() < 0);
    }

    @Test
    void conversionShouldBeReversible() {
        double amount = 100.0;

        CalculateRequest forwardRequest = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.USD)
                .setAmount(amount)
                .build();

        CalculateResponse forwardResponse = blockingStub.calculateRate(forwardRequest);
        double convertedAmount = forwardResponse.getCalculatedAmount();

        CalculateRequest backwardRequest = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.USD)
                .setDesiredCurrency(CurrencyValues.RUB)
                .setAmount(convertedAmount)
                .build();

        CalculateResponse backwardResponse = blockingStub.calculateRate(backwardRequest);

        Assertions.assertEquals(amount, backwardResponse.getCalculatedAmount());
    }

    @Test
    void usdToEurConversionShouldWork() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.USD)
                .setDesiredCurrency(CurrencyValues.EUR)
                .setAmount(100.0)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);

        Assertions.assertTrue(response.getCalculatedAmount() > 0);
    }

    @Test
    void rubToKztConversionShouldWork() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.KZT)
                .setAmount(1000.0)
                .build();

        CalculateResponse response = blockingStub.calculateRate(request);

        Assertions.assertTrue(response.getCalculatedAmount() > 0);
    }
}