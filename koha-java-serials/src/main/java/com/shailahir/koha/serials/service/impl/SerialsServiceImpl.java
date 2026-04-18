package com.shailahir.koha.serials.service.impl;

import com.shailahir.koha.serials.dto.*;
import com.shailahir.koha.serials.repository.SerialsRepository;
import com.shailahir.koha.serials.service.SerialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SerialsServiceImpl implements SerialsService {

    private final SerialsRepository repo;

    @Override
    public Page<SubscriptionDto> listSubscriptions(String query, Pageable pageable) {
        return repo.findAllSubscriptions(query, pageable);
    }

    @Override
    public SubscriptionDto addSubscription(SubscriptionDto dto) {
        return repo.insertSubscription(dto);
    }

    @Override
    public SubscriptionDto getSubscription(Long id) {
        return repo.findSubscriptionById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
    }

    @Override
    public SubscriptionDto updateSubscription(Long id, SubscriptionDto dto) {
        repo.findSubscriptionById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        return repo.updateSubscription(id, dto);
    }

    @Override
    public void deleteSubscription(Long id) {
        repo.deleteSubscription(id);
    }

    @Override
    public List<SerialDto> listSubscriptionSerials(Long subscriptionId) {
        return repo.findSerialsBySubscriptionId(subscriptionId);
    }

    @Override
    public List<SubscriptionFrequencyDto> listFrequencies() {
        return repo.findAllFrequencies();
    }

    @Override
    public SubscriptionFrequencyDto addFrequency(SubscriptionFrequencyDto dto) {
        return repo.insertFrequency(dto);
    }

    @Override
    public SubscriptionFrequencyDto getFrequency(Long id) {
        return repo.findFrequencyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Frequency not found"));
    }

    @Override
    public SubscriptionFrequencyDto updateFrequency(Long id, SubscriptionFrequencyDto dto) {
        repo.findFrequencyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Frequency not found"));
        return repo.updateFrequency(id, dto);
    }

    @Override
    public void deleteFrequency(Long id) {
        repo.deleteFrequency(id);
    }

    @Override
    public List<NumberingPatternDto> listNumberingPatterns() {
        return repo.findAllNumberingPatterns();
    }

    @Override
    public NumberingPatternDto addNumberingPattern(NumberingPatternDto dto) {
        return repo.insertNumberingPattern(dto);
    }

    @Override
    public NumberingPatternDto getNumberingPattern(Long id) {
        return repo.findNumberingPatternById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Numbering pattern not found"));
    }

    @Override
    public NumberingPatternDto updateNumberingPattern(Long id, NumberingPatternDto dto) {
        repo.findNumberingPatternById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Numbering pattern not found"));
        return repo.updateNumberingPattern(id, dto);
    }

    @Override
    public void deleteNumberingPattern(Long id) {
        repo.deleteNumberingPattern(id);
    }

    @Override
    public Page<SubscriptionDto> searchSubscriptions(String query, Pageable pageable) {
        return repo.findAllSubscriptions(query, pageable);
    }

    @Override
    public List<SerialDto> getSubscriptionHistory(Long subscriptionId) {
        return repo.findSerialsBySubscriptionId(subscriptionId);
    }

    @Override
    public SubscriptionDto renewSubscription(Long subscriptionId, SubscriptionDto dto) {
        repo.findSubscriptionById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        return repo.renewSubscription(subscriptionId, dto);
    }

    @Override
    public List<SerialDto> listClaims(Pageable pageable) {
        return repo.findClaimedSerials(pageable);
    }
}

