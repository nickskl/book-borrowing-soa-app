package ru.nikolay.service.storage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikolay.service.storage.domain.BookInformation;
import ru.nikolay.service.storage.domain.Storage;
import ru.nikolay.service.storage.repository.BookInformationRepository;
import ru.nikolay.service.storage.repository.StorageRepository;
import ru.nikolay.requests.BookInformationRequest;
import ru.nikolay.requests.StorageRequest;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageServiceImpl implements StorageService{
    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private BookInformationRepository bookInformationRepository;

    @Override
    @Transactional(readOnly = true)
    public Storage getById(String id) {
        Storage storage = storageRepository.findOne(id);
        if(storage == null) {
            throw new NullPointerException("Storage[" + id + "] not found in the database");
        }
        return storage;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Storage> getAllStoragesPaged(Pageable pageable) {
        return storageRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Storage> findStoragesByBookId(String bookId) {
        List<String> bookInformationIds = bookInformationRepository.findAllByBookId(bookId)
                .stream()
                .map(BookInformation::getId)
                .collect(Collectors.toList());
        return storageRepository.findAllByBookInformationResponseIdsIn(bookInformationIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookInformation> getBookInformationForStorageId(String id) {
        Storage storage = storageRepository.findOne(id);
        if(storage == null) {
            throw new NullPointerException("Storage[" + id + "] not found in the database");
        }
        List<BookInformation> bookInformationList = new ArrayList<>();
        bookInformationRepository.findAll(storage.getBookInformationResponseIds()).forEach(bookInformationList::add);

        return bookInformationList;
    }

    @Override
    @Transactional(readOnly = true)
    public BookInformation getBookInformationForStorageIdAndBookInformationId(String storageId,
                                                                              String bookInformationId) {
        Storage storage = storageRepository.findOne(storageId);
        if(storage == null) {
            throw new NullPointerException("Storage[" + storageId + "] not found in the database");
        }
        if(!storage.getBookInformationResponseIds().contains(bookInformationId)) {
            throw new IllegalArgumentException("Storage[" + storageId + "] does not contain book information[" +
                        bookInformationId + "]");
        }
        BookInformation bookInformation = bookInformationRepository.findOne(bookInformationId);
        if(bookInformation == null) {
            throw new NullPointerException("BookInformation[" + bookInformationId + "] not found in the database");
        }
        return bookInformation;
    }

    @Override
    @Transactional(readOnly = true)
    public BookInformation getBookInformationForStorageIdAndBookId(String storageId, String bookId) {
        Storage storage = storageRepository.findOne(storageId);
        if(storage == null) {
            throw new NullPointerException("Storage[" + storageId + "] not found in the database");
        }
        List<String> validBookInformations = bookInformationRepository.findAllByBookId(bookId).stream()
                .map(BookInformation::getId).collect(Collectors.toList());
        if(validBookInformations.isEmpty()) {
            throw new IllegalArgumentException("There is no information about book[" +
                    bookId + "]");
        }
        validBookInformations.retainAll(storage.getBookInformationResponseIds());
        if(validBookInformations.isEmpty()) {
            throw new IllegalArgumentException("Storage[" + storageId + "] does not contain book[" +
                    bookId + "]");
        }

        BookInformation bookInformation = bookInformationRepository.findOne(validBookInformations.get(0));
        if(bookInformation == null) {
            throw new NullPointerException("BookInformation[" + validBookInformations.get(0) +
                    "] not found in the database");
        }
        return bookInformation;
    }

    @Override
    @Transactional
    public Storage add(StorageRequest storageRequest) {
        List<BookInformationRequest> bookInformationRequestList = storageRequest.getBookInformationRequestList();
        List<String> bookInformationIdsList = new ArrayList<>();
        if(storageRequest.getBookInformationRequestList() != null) {
            for (BookInformationRequest bookInformationRequest : bookInformationRequestList) {
                bookInformationIdsList.add(bookInformationRepository
                        .save(new BookInformation(bookInformationRequest.getBookId(),
                                bookInformationRequest.getNumberLeft())).getId());
            }
        }

        return storageRepository.save(new Storage()
                .setLocation(storageRequest.getLocation())
                .setBookInformationResponseIds(bookInformationIdsList));
    }

    @Override
    @Transactional
    public void delete(String id)
    {
        Storage storage = storageRepository.findOne(id);
        if(storage != null) {
            List<String> bookInformationIdsList = storage.getBookInformationResponseIds();
            for(String bookId : bookInformationIdsList) {
                bookInformationRepository.delete(bookId);
            }
            storageRepository.delete(id);
        }
    }

    @Override
    @Transactional
    public BookInformation addBookInformationForStorageId(String id, BookInformationRequest bookInformationRequest) {
        Storage storageToUpdate = storageRepository.findOne(id);
        if (storageToUpdate == null) {
            throw new NullPointerException("Storage[" + id + "] not found in the database");
        }
        if (bookInformationRequest.getNumberLeft() < 0) {
            throw new IllegalArgumentException("Number left should be equal or greater than 0");
        }

        BookInformation bookInformationToAdd = bookInformationRepository.save(new BookInformation(
                bookInformationRequest.getBookId(), bookInformationRequest.getNumberLeft()));

        storageToUpdate.getBookInformationResponseIds().add(bookInformationToAdd.getId());
        storageRepository.save(storageToUpdate);

        return bookInformationToAdd;
    }

    @Override
    @Transactional
    public BookInformation updateBookInformationForStorageId(String id, String bookInformationId,
                                                      BookInformationRequest bookInformationRequest) {
        Storage storageToUpdate = storageRepository.findOne(id);
        if (storageToUpdate == null) {
            throw new NullPointerException("Storage[" + id + "] not found in the database");
        }

        if(!storageToUpdate.getBookInformationResponseIds().contains(bookInformationId)) {
            throw new IllegalArgumentException("Storage[" + id + "] does not contain book information[" +
                    bookInformationId + "]");
        }

        BookInformation bookInformationToUpdate = bookInformationRepository.findOne(bookInformationId);
        if(bookInformationToUpdate == null) {
            throw new NullPointerException("BookInformation[" + bookInformationId +
                    "] not found in the database");
        }

        bookInformationToUpdate.setBookId(bookInformationRequest.getBookId());
        bookInformationToUpdate.setNumberLeft(bookInformationRequest.getNumberLeft());
        bookInformationRepository.save(bookInformationToUpdate);

        return bookInformationToUpdate;
    }

    @Override
    @Transactional
    public void deleteBookInformationForStorageId(String storageId, String bookInformationId) {
        Storage storageToUpdate = storageRepository.findOne(storageId);
        if (storageToUpdate == null) {
            throw new NullPointerException("Storage[" + storageId + "] not found in the database");
        }
        if (storageToUpdate.getBookInformationResponseIds().stream().anyMatch(id -> id.equals(bookInformationId))) {
            storageToUpdate.getBookInformationResponseIds().remove(bookInformationId);
            bookInformationRepository.delete(bookInformationId);
            storageRepository.save(storageToUpdate);
        } else {
            throw new IllegalArgumentException("Book[" + bookInformationId + "] not found in storage[" + storageId + "]");
        }
    }

    @Override
    @Transactional
    public void deleteBookInformationForStorageIdByBookId(String storageId, String bookId) {
        Storage storageToUpdate = storageRepository.findOne(storageId);
        if (storageToUpdate == null) {
            throw new NullPointerException("Storage[" + storageId + "] not found in the database");
        }
        List<String> bookInfoIdsInStorage = storageToUpdate.getBookInformationResponseIds();
        List<String> bookInfoIdsToDelete = bookInformationRepository
                .findAllByBookId(bookId)
                .stream()
                .map(BookInformation::getId)
                .filter(bookInfoIdsInStorage::contains)
                .collect(Collectors.toList());
        bookInfoIdsInStorage.removeAll(bookInfoIdsToDelete);
        for(String bookInfoIdToDelete : bookInfoIdsToDelete) {
            bookInformationRepository.delete(bookInfoIdToDelete);
        }
        storageRepository.save(storageToUpdate);
    }
}
