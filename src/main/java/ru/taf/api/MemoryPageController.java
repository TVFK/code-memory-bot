package ru.taf.api;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.taf.entity.MemoryPage;
import ru.taf.entity.Person;
import ru.taf.service.MemoryPageService;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/memory_pages")
public class MemoryPageController {

    private final MemoryPageService memoryPageService;

    @GetMapping("{pageId:\\d+}")
    public String getMemoryPage(@PathVariable("pageId") Long id,
                                Model model){
        MemoryPage memoryPage = memoryPageService.findById(id);
        Person person = memoryPage.getPerson();
        String base64Photo = Base64.encodeBase64String(person.getPhoto());
        model.addAttribute("person", person);
        model.addAttribute("base64Photo", base64Photo);
        return "/memorypages/memory_page";
    }
}
