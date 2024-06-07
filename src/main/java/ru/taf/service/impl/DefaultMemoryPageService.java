package ru.taf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.taf.entity.MemoryPage;
import ru.taf.exception.MemoryPageNotFoundException;
import ru.taf.repository.MemoryPageRepository;
import ru.taf.service.MemoryPageService;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class DefaultMemoryPageService implements MemoryPageService {

    private final MemoryPageRepository memoryPageRepository;

    @Override
    public MemoryPage findById(Long id) {
        return memoryPageRepository.findById(id).orElse(null);
    }

    @Override
    public ByteArrayOutputStream generatePdf(Long id) {
        return null;
    }
}
