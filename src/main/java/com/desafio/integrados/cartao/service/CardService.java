package com.desafio.integrados.cartao.service;

import com.desafio.integrados.cartao.domain.Card;
import com.desafio.integrados.cartao.domain.CardInvoice;
import com.desafio.integrados.cartao.dto.CardInvoiceResponse;
import com.desafio.integrados.cartao.dto.CardResponse;
import com.desafio.integrados.cartao.repository.CardInvoiceRepository;
import com.desafio.integrados.cartao.repository.CardRepository;
import com.desafio.integrados.usuario.domain.User;
import com.desafio.integrados.usuario.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardInvoiceRepository cardInvoiceRepository;
    private final UserRepository userRepository;

    public CardService(CardRepository cardRepository, CardInvoiceRepository cardInvoiceRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.cardInvoiceRepository = cardInvoiceRepository;
        this.userRepository = userRepository;
    }

    public List<CardResponse> getMyCards(Long userId) {
        return cardRepository.findByUserId(userId).stream()
                .map(c -> new CardResponse(
                        c.getId(), c.getNameOnCard(), c.getCardNumber(), c.getValidThru(),
                        c.getCardType(), c.getLimitAmount(), c.getActive(), c.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public List<CardInvoiceResponse> getMyInvoices(Long userId) {
        return cardInvoiceRepository.findByUserIdOrderByDueDateDesc(userId).stream()
                .map(i -> new CardInvoiceResponse(
                        i.getId(), i.getCard().getId(), i.getAmount(), i.getStatus(),
                        i.getDueDate(), i.getReferenceMonth(), i.getReferenceYear(), i.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
