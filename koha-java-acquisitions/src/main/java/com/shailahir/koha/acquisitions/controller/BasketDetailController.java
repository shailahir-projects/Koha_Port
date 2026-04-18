package com.shailahir.koha.acquisitions.controller;
import lombok.extern.slf4j.Slf4j;

import com.shailahir.koha.acquisitions.dto.*;
import com.shailahir.koha.acquisitions.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller porting all operations from basket.pl.
 *
 * <pre>
 *  GET    /acquisitions/baskets/{basketno}            — op=list (full detail view)
 *  POST   /acquisitions/baskets/{basketno}/close      — op=cud-close
 *  POST   /acquisitions/baskets/{basketno}/reopen     — op=cud-reopen
 *  DELETE /acquisitions/baskets/{basketno}            — op=cud-delete
 *  DELETE /acquisitions/baskets/{basketno}/orders/{ordernumber} — op=cud-delete-order
 *  PUT    /acquisitions/baskets/{basketno}/users      — op=cud-mod_users
 *  PUT    /acquisitions/baskets/{basketno}/branch     — op=cud-mod_branch
 *  GET    /acquisitions/baskets/{basketno}/export     — op=export (CSV)
 * </pre>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class BasketDetailController {

    private final BasketService basketService;

    /**
     * Full basket detail — equivalent to basket.pl op=list.
     * Returns basket metadata, all active and cancelled order lines
     * enriched with biblio/budget/holds info, and tax-rate footer totals.
     *
     * @param basketno    basket number
     * @param duplinbatch optional flag indicating a duplicate was found during the last import
     */
    @GetMapping("/acquisitions/baskets/{basketno}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BasketDetailDto> getBasketDetail(
            @PathVariable Long basketno,
            @RequestParam(value = "duplinbatch", required = false) String duplinbatch) {
        log.debug("Entering getBasketDetail - {}, {}", basketno, duplinbatch);
        return ResponseEntity.ok(basketService.getBasketDetail(basketno, duplinbatch));
    }

    /**
     * Close a basket — op=cud-close.
     * <p>
     * When {@code confirm=true}: closes the basket immediately.
     * When {@code create_basket_group=true}: also creates a basket group, attaches the basket, and closes the group.
     * Returns the new basket group id in the body when one was created, or an empty object.
     */
    @PostMapping("/acquisitions/baskets/{basketno}/close", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map<String, Object>> closeBasket(
            @PathVariable Long basketno,
            @RequestBody BasketCloseRequest request) {
        log.debug("Entering closeBasket - {}, {}", basketno, request);
        Long basketgroupid = basketService.closeBasket(basketno, request);
        if (basketgroupid != null) {
            return ResponseEntity.ok(Map.of("basketgroupid", basketgroupid));
        }
        return ResponseEntity.ok(Map.of("closed", true));
    }

    /**
     * Reopen a closed basket — op=cud-reopen.
     * Clears the closedate so the basket can be edited again.
     */
    @PostMapping("/acquisitions/baskets/{basketno}/reopen", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> reopenBasket(@PathVariable Long basketno) {
        log.debug("Entering reopenBasket - {}", basketno);
        basketService.reopenBasket(basketno);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a basket — op=cud-delete.
     * Cancels all active orders and then removes the basket row.
     * Requires delete_baskets permission (enforced at gateway/auth layer).
     */
    @DeleteMapping("/acquisitions/baskets/{basketno}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteBasket(@PathVariable Long basketno) {
        log.debug("Entering deleteBasket - {}", basketno);
        basketService.deleteBasket(basketno);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a single cancelled order with no biblionumber — op=cud-delete-order.
     * Only cancelled orders without an attached biblio may be hard-deleted.
     */
    @DeleteMapping("/acquisitions/baskets/{basketno}/orders/{ordernumber}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteCancelledOrder(
            @PathVariable Long basketno,
            @PathVariable Long ordernumber) {
        log.debug("Entering deleteCancelledOrder - {}, {}", basketno, ordernumber);
        basketService.deleteCancelledOrder(ordernumber);
        return ResponseEntity.noContent().build();
    }

    /**
     * Replace basket user list — op=cud-mod_users.
     * Body: {@code { "users_ids": "1:2:3" }} (colon-separated patron ids).
     */
    @PutMapping("/acquisitions/baskets/{basketno}/users", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> setBasketUsers(
            @PathVariable Long basketno,
            @RequestBody Map<String, String> body) {
        log.debug("Entering setBasketUsers - {}, {}", basketno, body);
        String usersIds = body.getOrDefault("users_ids", "");
        List<Long> userIds = Arrays.stream(usersIds.split(":"))
                .filter(s -> !s.isBlank())
                .map(Long::parseLong)
                .collect(Collectors.toList());
        basketService.setBasketUsers(basketno, userIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update basket branch — op=cud-mod_branch.
     * Body: {@code { "branch": "CPL" }}. Send an empty string to clear the branch.
     */
    @PutMapping("/acquisitions/baskets/{basketno}/branch", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> setBasketBranch(
            @PathVariable Long basketno,
            @RequestBody Map<String, String> body) {
        log.debug("Entering setBasketBranch - {}, {}", basketno, body);
        basketService.setBasketBranch(basketno, body.get("branch"));
        return ResponseEntity.noContent().build();
    }

    /**
     * Export basket as CSV — op=export.
     * Returns a CSV file attachment named {@code basket{basketno}.csv}.
     * Mirrors GetBasketAsCSV().
     */
    @GetMapping("/acquisitions/baskets/{basketno}/export", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<byte[]> exportBasketAsCsv(@PathVariable Long basketno) {
        log.debug("Entering exportBasketAsCsv - {}", basketno);
        String csv = basketService.exportBasketAsCsv(basketno);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "basket" + basketno + ".csv");
        headers.setContentLength(bytes.length);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}

