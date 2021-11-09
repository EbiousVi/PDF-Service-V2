package com.example.pdf.controller;

import com.example.pdf.domain.dto.AddFormDto;
import com.example.pdf.domain.entity.Namespace;
import com.example.pdf.exception.CustomDBException;
import com.example.pdf.service.h2db.FilenameService;
import com.example.pdf.service.h2db.NamespaceService;
import com.example.pdf.service.h2db.NamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/naming")
@Validated
public class NamingController {
    private final FilenameService filenameService;
    private final NamespaceService namespaceService;
    private final NamingService namingService;

    @Autowired
    public NamingController(FilenameService filenameService, NamespaceService namespaceService,
                            NamingService namingService) {
        this.filenameService = filenameService;
        this.namespaceService = namespaceService;
        this.namingService = namingService;
    }

    @GetMapping("/filenames-by-namespace")
    public List<String> getFilenamesByNamespace(@RequestParam("namespace")
                                                @NotBlank(message = "{namespace.blank}")
                                                @Size(min = 3, max = 255, message = "{namespace.size}")
                                                        String name) throws CustomDBException {
        return namespaceService.getFilenamesByNamespace(name);
    }

    @GetMapping("/namespace")
    public Namespace getNamespaceByName(@RequestParam("namespace")
                                        @NotBlank(message = "{namespace.blank}")
                                        @Size(min = 3, max = 255, message = "{namespace.size}")
                                                String name) throws CustomDBException {
        return namespaceService.getNamespaceByName(name);
    }

    @GetMapping("/namespaces")
    public List<String> getAllNamespaces() {
        return namespaceService.getAllNamespace()
                .stream()
                .map(Namespace::getName)
                .collect(Collectors.toList());
    }

    @GetMapping("/namespace-and-filenames")
    public Map<String, List<String>> getAllNamespacesAndTheirFilenames() throws CustomDBException {
        return namingService.collectAllNamespacesAndTheirFilenames();
    }

    @GetMapping("/add-namespace")
    public void addNamespace(@RequestParam("namespace")
                             @NotBlank(message = "{namespace.blank}")
                             @Size(min = 3, max = 255, message = "{namespace.size}")
                                     String name) throws CustomDBException {
        namespaceService.addNamespace(name);
    }

    @PostMapping("/add-filename")
    public void addFilenameToNamespace(@Valid @RequestBody AddFormDto addFormDto) throws CustomDBException {
        namingService.addFilenameToNamespace(addFormDto);
    }

    @DeleteMapping("/delete-namespace/{namespace}")
    public void deleteNamespace(@PathVariable("namespace")
                                @NotBlank(message = "{namespace.blank}")
                                @Size(min = 3, max = 255, message = "{namespace.size}")
                                        String name) throws CustomDBException {
        namespaceService.deleteNamespaceByName(name);
    }

    @DeleteMapping("/delete-filename/{namespace}/{filename}")
    public void deleteFilename(@PathVariable("namespace")
                               @NotBlank(message = "{namespace.blank}")
                               @Size(min = 3, max = 255, message = "{namespace.size}")
                                       String namespace,
                               @PathVariable("filename")
                               @NotBlank(message = "{filename.blank}")
                               @Size(min = 3, max = 255, message = "{filename.size}")
                                       String filename) throws CustomDBException {
        filenameService.deleteFilename(filename, namespace);
    }

    @GetMapping("fill")
    public void fill() throws CustomDBException {
        namingService.personal(map);
    }

    Map<String, String[]> map = new HashMap<>();

    {
        map.put("ТЭКТОРГ", tek);
        map.put("ГПБ МАЛЫЙ Газнефтеторг 1", gazneftetorg);
        map.put("ГПБ МАЛЫЙ Газнефтеторг 2", gazneftetorgOld);
        map.put("ГПБ МАЛЫЙ С ПЕРЕТОРЖКАМИ", smallTorg);
        map.put("ГПБ БОЛЬШОЙ", gpbBig);
    }

    static String[] tek = {
            "Коммерческое предложение PDF",
            "Техническое предложение PDF",
            "Форма 1а Сведения об участнике",
            "Форма 2 Информация о собственниках",
            "Форма 3 Опыт поставок",
            "Форма 4 Сведения о мат-технических ресурсах",
            "Форма 5 Сведения о кадровых ресурсах",
            "Форма 6 Cогласие на обработку данных физ-лица",
            "Форма 7 Cогласие на обработку данных юр-лица",
            "Форма 8 Техническое предложение",
            "Форма 9 Письмо о подаче Заявки",
            "Форма 17 Российское происхождение товаров",
            "К Форме 17 Перечень товаров Российского происхождения",
            "Форма 15 Опись документов Квал-часть",
            "Форма 15 Опись документов Тех-часть",
            "Форма 15 Опись документов Комм-часть",
            "Письмо об отсутствие судебных разбирательств с ПАО «НК «Роснефть»",
            "Письмо об отсутствии проведении технического аудита",
            "Письмо согласие с условиями договора",
            "Письмо о соответствии продукции",
            "Письмо согласие с установленными в документации условиями оплаты",
            "Письмо согласие с установленными в документации базисом поставки",
            "Письмо о соответствии ТП с установленными в документации требованиями",
            "Согласие с условиями технического задания",
            "Письмо о не нахождении в процессе ликвидации",
            "Справка о заключение крупной сделки",
            "Согласие Участника на проведение выездной проверки",
            "Письмо об отсутствии судебных разбирательств с ПАО-НК-Роснефть",
    };

    static String[] gazneftetorg = {
            "Форма коммерческого предложения PDF",
            "Форма 1 Письмо о подаче Заявки PDF",
            "Форма 2 Форма Опись документов",
            "Форма 4 Форма декларации соответствия участника закупки",
            "Форма 5 Форма согласия физического лица на обработку своих персональных данных",
            "Форма 6 Форма сведений о цепочке собственников",
            "Форма 7 Форма справки об опыте поставок",
            "Форма 8 Форма справки о материально-технических ресурсах",
            "Форма 9 Форма справки о кадровых ресурсах",
            "Форма 10 Форма справки о деловой репутации",
            "Форма 11 Форма справки о финансовом положении",
            "Справка о заключении крупной сделки"};

    static String[] gazneftetorgOld = {
            "Форма 3 Коммерческое предложение PDF",
            "Форма 1 Письмо о подаче Заявки на участие",
            "Форма 2 Опись документов",
            "Форма 4 Декларация соответствия Участника",
            "Форма 5 Анкета Участника",
            "Форма 6 Сведения о цепочке собственников",
            "Форма 10 Справка о деловой репутации",
            "Форма 10 Согласие на обработку персональных данных",
            "Справка о заключении крупной сделки"
    };

    static String[] gpbBig = {
            "Форма расчета цены КП PDF",
            "Форма 1 Письмо о подаче Заявки",
            "Форма 1_1 Ценовое предложение",
            "Форма 1_2 Техническое предложение Таблица 2",
            "Форма 2 Анкета Участника",
            "Форма 2_1 Информация о цепочке собственников",
            "Форма 2_2 Согласие на обработку данных",
            "Форма 3 Справка об опыте поставок",
            "Форма 3 Справка о выполнении договоров",
            "Форма 4 Справка о соответствии Участника",
            "Форма 7 Опись документов",
            "Справка о заключении крупной сделки"
    };

    static String[] smallTorg = {
            "Форма КП PDF",
            "Форма ТП PDF",
            "Форма 1 Письмо о подаче Заявки",
            "Форма 2 Анкета Участника",
            "Форма 2_1 Информация о цепочке собственников",
            "Форма 2_2 Согласие на обработку данных",
            "Форма 6 Сведения о субпоставщиках",
            "Форма 7 Справка о деловой репутации",
            "Форма 8 Декларация о соответствии",
            "Форма 11 График поставки",
            "Форма 12 Опись",
            "Справка о заключении крупной сделки"
    };
}
