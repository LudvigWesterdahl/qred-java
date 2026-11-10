package com.qred.payment.api.payment;

import com.qred.payment.api.DomainException;
import com.qred.payment.api.payment.dtos.PaymentV1TypeDto;
import com.qred.payment.api.payment.requests.CreatePaymentV1Request;
import com.qred.payment.api.payment.requests.FilterPaymentV1Request;
import com.qred.payment.api.payment.responses.PaymentV1Response;
import com.qred.payment.data.ClientEntity;
import com.qred.payment.data.ContractEntity;
import com.qred.payment.data.ContractRepository;
import com.qred.payment.data.PaymentEntity;
import com.qred.payment.data.PaymentRepository;
import com.qred.payment.data.TransactionHelper;
import com.qred.payment.helpers.CurrentDateHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public final class PaymentV1ServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 10);

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ContractRepository contractRepository;
    @Mock
    private TransactionHelper transactionHelper;

    private PaymentV1Service service;

    private static FilterPaymentV1Request createFilterRequest(final String contractNumber) {
        final FilterPaymentV1Request request = new FilterPaymentV1Request();
        request.setContract_number(contractNumber);
        return request;
    }

    private static CreatePaymentV1Request createPaymentRequest(final LocalDate paymentDate,
                                                               final String amount,
                                                               final PaymentV1TypeDto type,
                                                               final String contractNumber) {
        return new CreatePaymentV1Request(paymentDate, new BigDecimal(amount), type, contractNumber);
    }

    private static ContractEntity createContractEntity(final String contractNumber, final String clientNumber) {
        final ClientEntity client = new ClientEntity();
        client.setClientNumber(clientNumber);

        final ContractEntity contract = new ContractEntity();
        contract.setContractNumber(contractNumber);
        contract.setClient(client);
        return contract;
    }

    private static PaymentEntity createPaymentEntity(final Long id,
                                                     final LocalDate paymentDate,
                                                     final String amount,
                                                     final String type,
                                                     final String contractNumber,
                                                     final String clientNumber) {
        final PaymentEntity payment = new PaymentEntity();
        payment.setId(id);
        payment.setPaymentDate(paymentDate);
        payment.setAmount(new BigDecimal(amount));
        payment.setType(type);
        payment.setContract(createContractEntity(contractNumber, clientNumber));
        return payment;
    }

    @BeforeEach
    void setUp() {
        service = new PaymentV1Service(
                paymentRepository,
                contractRepository,
                transactionHelper,
                new CurrentDateHelper(Instant.parse("2026-11-10T12:00:00Z"))
        );

        lenient().doAnswer(invocation -> {
            final TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        }).when(transactionHelper).execute(any());

        lenient().doAnswer(invocation -> {
            final Consumer<?> action = invocation.getArgument(0);
            action.accept(null);
            return null;
        }).when(transactionHelper).executeWithoutResult(any());
    }

    @Test
    void getPayments_Should_throwException_When_requestIsNull() {
        assertThatThrownBy(() -> service.getPayments(null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void getPayments_Should_returnEmptyList_When_noPaymentsExist() {
        final FilterPaymentV1Request request = createFilterRequest("101");
        doReturn(List.of()).when(paymentRepository).findAllByContractContractNumber(request.getContract_number());

        final List<PaymentV1Response> actual = service.getPayments(request);

        assertThat(actual).isEqualTo(List.of());
        verify(paymentRepository).findAllByContractContractNumber(request.getContract_number());
    }

    @Test
    void getPayments_Should_returnMappedPayments_When_paymentsExist() {
        final FilterPaymentV1Request request = createFilterRequest("101");
        final PaymentEntity payment = createPaymentEntity(10L, LocalDate.of(2026, 1, 15), "100.00", "incoming", request.getContract_number(), "1");
        doReturn(List.of(payment)).when(paymentRepository).findAllByContractContractNumber(request.getContract_number());

        final List<PaymentV1Response> actual = service.getPayments(request);

        assertThat(actual.size()).isEqualTo(1);
        final PaymentV1Response response = actual.get(0);
        assertThat(response.getId()).isEqualTo(payment.getId());
        assertThat(response.getPaymentDate()).isEqualTo(payment.getPaymentDate());
        assertThat(response.getAmount()).isEqualTo(payment.getAmount());
        assertThat(response.getType()).isEqualTo(PaymentV1TypeDto.fromString(payment.getType()).orElseThrow());
        assertThat(response.getContractNumber()).isEqualTo(payment.getContract().getContractNumber());
        assertThat(response.getClientNumber()).isEqualTo(payment.getContract().getClient().getClientNumber());
    }

    @Test
    void createPayment_Should_throwException_When_requestIsNull() {
        assertThatThrownBy(() -> service.createPayment(null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void createPayment_Should_throwException_When_paymentDateIsFuture() {
        final CreatePaymentV1Request request = createPaymentRequest(TODAY.plusDays(1), "10.00", PaymentV1TypeDto.INCOMING, "101");

        assertThatThrownBy(() -> service.createPayment(request))
                .isInstanceOf(DomainException.class)
                .extracting("errorCode")
                .isEqualTo("PAYMENT_DATE_FUTURE");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_Should_throwException_When_amountIsZero() {
        final CreatePaymentV1Request zero = createPaymentRequest(TODAY, "0", PaymentV1TypeDto.INCOMING, "101");

        assertThatThrownBy(() -> service.createPayment(zero))
                .isInstanceOf(DomainException.class)
                .extracting("errorCode")
                .isEqualTo("AMOUNT_NON_POSITIVE");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_Should_throwException_When_amountIsNegative() {
        final CreatePaymentV1Request negative = createPaymentRequest(TODAY, "-1.00", PaymentV1TypeDto.OUTGOING, "101");

        assertThatThrownBy(() -> service.createPayment(negative))
                .isInstanceOf(DomainException.class)
                .extracting("errorCode")
                .isEqualTo("AMOUNT_NON_POSITIVE");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_Should_throwException_When_amountHasTooManyDecimals() {
        final CreatePaymentV1Request request = createPaymentRequest(TODAY, "10.001", PaymentV1TypeDto.INCOMING, "101");

        assertThatThrownBy(() -> service.createPayment(request))
                .isInstanceOf(DomainException.class)
                .extracting("errorCode")
                .isEqualTo("AMOUNT_TOO_MANY_DECIMALS");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_Should_throwException_When_contractIsMissing() {
        final CreatePaymentV1Request request = createPaymentRequest(TODAY, "10.00", PaymentV1TypeDto.INCOMING, "999");
        doReturn(Optional.empty()).when(contractRepository).findByContractNumber(request.getContractNumber());

        assertThatThrownBy(() -> service.createPayment(request))
                .isInstanceOf(DomainException.class)
                .extracting("errorCode")
                .isEqualTo("CONTRACT_MISSING");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_Should_save_When_requestIsValid() {
        final CreatePaymentV1Request request = createPaymentRequest(TODAY, "10.00", PaymentV1TypeDto.INCOMING, "101");
        final ContractEntity contract = createContractEntity(request.getContractNumber(), "1");
        doReturn(Optional.of(contract)).when(contractRepository).findByContractNumber(request.getContractNumber());

        service.createPayment(request);

        final ArgumentCaptor<PaymentEntity> captor = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(captor.capture());
        final PaymentEntity actual = captor.getValue();
        assertThat(actual.getPaymentDate()).isEqualTo(request.getPaymentDate());
        assertThat(actual.getAmount()).isEqualTo(request.getAmount());
        assertThat(actual.getType()).isEqualTo(request.getType().toString());
        assertThat(actual.getContract()).isEqualTo(contract);
    }

    @Test
    void createPayment_Should_save_When_paymentDateIsPast() {
        final CreatePaymentV1Request request = createPaymentRequest(TODAY.minusDays(1), "10.50", PaymentV1TypeDto.OUTGOING, "101");
        final ContractEntity contract = createContractEntity(request.getContractNumber(), "1");
        doReturn(Optional.of(contract)).when(contractRepository).findByContractNumber(request.getContractNumber());

        service.createPayment(request);

        final ArgumentCaptor<PaymentEntity> captor = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(captor.capture());
        final PaymentEntity actual = captor.getValue();
        assertThat(actual.getPaymentDate()).isEqualTo(request.getPaymentDate());
        assertThat(actual.getType()).isEqualTo(request.getType().toString());
    }
}
