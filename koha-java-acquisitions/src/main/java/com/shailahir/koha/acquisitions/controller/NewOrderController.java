package com.shailahir.koha.acquisitions.controller;

import com.shailahir.koha.acquisitions.dto.NewOrderSeedDto;
import com.shailahir.koha.acquisitions.repository.AcquisitionsExtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller porting neworderempty.pl, newordersubscription.pl,
 * and newordersuggestion.pl.
 *
 * <p>These three Perl scripts all populate a "new order" form with seed data
 * from different sources (blank, subscription, or purchase suggestion).
 * Here they are exposed as GET endpoints that return the JSON/XML seed data
 * a client uses to pre-fill the order creation form.
 *
 * <pre>
 * GET /api/v1/acquisitions/baskets/{basketno}/new-order/empty[?biblionumber=]
 * GET /api/v1/acquisitions/baskets/{basketno}/new-order/from-subscription?subscriptionid=
 * GET /api/v1/acquisitions/baskets/{basketno}/new-order/from-suggestion?suggestionid=
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/acquisitions/baskets")
@RequiredArgsConstructor
@Slf4j
public class NewOrderController {

    private final AcquisitionsExtRepository extRepo;

    /**
     * Returns seed data for a blank new order form.
     * Mirrors neworderempty.pl (with optional biblionumber pre-fill).
     *
     * @param basketno     the open basket to attach the new order to
     * @param biblionumber optional existing biblio to pre-fill from
     */
    @GetMapping(
            value = "/{basketno}/new-order/empty",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<NewOrderSeedDto> newOrderEmpty(
            @PathVariable Long basketno,
            @RequestParam(required = false) Long biblionumber) {

        log.debug("GET new-order/empty basketno={} biblionumber={}", basketno, biblionumber);
        return extRepo.getNewOrderSeed(basketno, biblionumber != null ? biblionumber : 0L)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns seed data for a new order pre-filled from an existing subscription.
     * Mirrors newordersubscription.pl.
     *
     * @param basketno       the basket
     * @param subscriptionid the subscription to link
     */
    @GetMapping(
            value = "/{basketno}/new-order/from-subscription",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<NewOrderSeedDto> newOrderFromSubscription(
            @PathVariable Long basketno,
            @RequestParam Long subscriptionid) {

        log.debug("GET new-order/from-subscription basketno={} subscriptionid={}", basketno, subscriptionid);
        return extRepo.getNewOrderSeedFromSubscription(basketno, subscriptionid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns seed data for a new order pre-filled from a purchase suggestion.
     * Mirrors newordersuggestion.pl.
     *
     * @param basketno     the basket
     * @param suggestionid the purchase suggestion
     */
    @GetMapping(
            value = "/{basketno}/new-order/from-suggestion",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public ResponseEntity<NewOrderSeedDto> newOrderFromSuggestion(
            @PathVariable Long basketno,
            @RequestParam Long suggestionid) {

        log.debug("GET new-order/from-suggestion basketno={} suggestionid={}", basketno, suggestionid);
        return extRepo.getNewOrderSeedFromSuggestion(basketno, suggestionid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

