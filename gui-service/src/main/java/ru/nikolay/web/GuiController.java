package ru.nikolay.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import ru.nikolay.remote.RemoteGatewayService;
import ru.nikolay.remote.RemoteServiceStatusException;
import ru.nikolay.requests.BookRequest;
import ru.nikolay.requests.BorrowingInfoRequest;
import ru.nikolay.requests.StorageRequest;
import ru.nikolay.service.gateway.web.model.BookComposite;
import ru.nikolay.service.gateway.web.model.BorrowingInfoComposite;
import ru.nikolay.service.gateway.web.model.StorageComposite;

import javax.validation.Valid;

@Controller
@RequestMapping("/")
public class GuiController {

    @Autowired
    RemoteGatewayService gatewayService;

    public static boolean isAdmin = false;

    @GetMapping("/book")
    public String showAllBooks(Model model, Pageable pageable) {
        Page<BookComposite> _page = gatewayService.getBooksPaged(pageable);
        PageWrapper<BookComposite> page = new PageWrapper<BookComposite>(_page, "/book");
        model.addAttribute("books", page.getContent());
        model.addAttribute("page", page);
        return "book/books";
    }

    @GetMapping("book/{id}")
    public String showBook(@PathVariable String id, Model model) {
        model.addAttribute("book", gatewayService.getBook(id));
        return "book/bookdetailed";
    }

    @GetMapping("book/edit/{id}")
    public String edit(@PathVariable String id, Model model) {
        model.addAttribute("book", gatewayService.getBook(id));
        model.addAttribute("bookId", id);
        return "book/bookedit";
    }

    @GetMapping("book/new")
    public String newBook(Model model) {
        model.addAttribute("book", new BookRequest());
        return "book/bookform";
    }

    @PostMapping("book/new")
    public String saveBook(@ModelAttribute("book") @Valid BookRequest book, BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            return "book/bookform";
        }
        BookComposite result = gatewayService.createBook(book.getTitle(),book.getAuthor(), book.getDescription());
        return "redirect:/book/" + result.getBookId();
    }

    @PostMapping("book/edit/{id}")
    public String editBook(@PathVariable String id, @ModelAttribute("book") @Valid BookRequest book, BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            return "book/bookedit";
        }
        gatewayService.updateBook(id, book.getTitle(),book.getAuthor(), book.getDescription());
        return "redirect:/book/" + id;
    }

    @GetMapping("book/delete/{id}")
    public String delete(@PathVariable String id) {
        gatewayService.deleteBook(id);
        return "redirect:/book";
    }

    @GetMapping("/storage")
    public String showAllStorages(Model model, Pageable pageable) {
        Page<StorageComposite> _page = gatewayService.getAllStoragesPaged(pageable);
        PageWrapper<StorageComposite> page = new PageWrapper<StorageComposite>(_page, "/storage");
        model.addAttribute("storages", page.getContent());
        model.addAttribute("page", page);
        return "storage/storage";
    }

    @GetMapping("storage/{id}")
    public String showStorage(@PathVariable String id, Model model) {
        model.addAttribute("storage", gatewayService.getStorage(id));
        return "storage/storagedetailed";
    }

    @GetMapping("storage/new")
    public String newStorage(Model model) {
        model.addAttribute("storage", new StorageRequest());
        return "storage/storageform";
    }

    @PostMapping("storage/new")
    public String saveStorage(@ModelAttribute("storage") @Valid StorageRequest storage, BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            return "storage/storageform";
        }
        StorageComposite result = gatewayService.createStorage(storage.getLocation());
        return "redirect:/storage/" + result.getStorageId();
    }

    @GetMapping("borrow/request")
    public String newBorrowingRequest(Model model) {
        GuiBorrowRequest request = new GuiBorrowRequest();
        request.setUserId("5a21cbd590b5b120a4504a43");
        model.addAttribute("borrow", request);
        return "borrow/borrowrequest";
    }

    @PostMapping("borrow/request")
    public String saveBorrowingRequest(@ModelAttribute("borrow") @Valid GuiBorrowRequest _request, BindingResult errors, Model model) throws  Exception {
        if (errors.hasErrors()) {
            return "borrow/borrowrequest";
        }
        BorrowingInfoRequest request = _request.toBorrowingInfoRequest();
        try {
            BorrowingInfoComposite result = gatewayService.borrowBook(request.getUserId(), request.getBookId(), request.getStorageId(),
                    request.getBorrowedDate(), request.getDateToReturnBook());
            model.addAttribute("borrow", result);
            return "borrow/borrowstub";
        }
        catch (HttpStatusCodeException ex) {
            if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RemoteServiceStatusException(ex.getMessage(), ex.getStatusCode());
            }
        }
        return null;
    }

    @GetMapping("/statistics")
    public String showStatistics(Model model) {

        int bookBorrowings = gatewayService.countBookBorrowings();
        int bookViewedToday = gatewayService.countBookViewed();
        int storagesViewedToday = gatewayService.countStoragesViewed();

        model.addAttribute("borrowings", bookBorrowings);
        model.addAttribute("bookviews", bookViewedToday);
        model.addAttribute("storageviews", storagesViewedToday);

        return "statistics/statistics";
    }

}
