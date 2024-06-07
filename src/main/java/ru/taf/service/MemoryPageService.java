package ru.taf.service;

import ru.taf.entity.MemoryPage;

import java.io.ByteArrayOutputStream;

public interface MemoryPageService {
    MemoryPage findById(Long id);

    ByteArrayOutputStream generatePdf(Long id);
}
