package com.canbe.HotelBooking.payment;

import com.canbe.HotelBooking.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<Response> createPayment(@RequestBody PaymentRequest paymentRequest){
        return ResponseEntity.ok(paymentService.createPayment(paymentRequest));
    }

    @PutMapping("/update")
    public void updatePayment(@RequestBody PaymentRequest paymentRequest){
        paymentService.updatePayment(paymentRequest);
    }
}
