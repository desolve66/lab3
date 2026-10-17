package com.example.newsroom.controller;

import com.example.newsroom.dto.EditorialSectionDto;
import com.example.newsroom.model.EditorialSection;
import com.example.newsroom.service.EditorialSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editorial-sections")
@RequiredArgsConstructor
public class EditorialSectionController {
    private final EditorialSectionService sectionService;

    private EditorialSectionDto toDto(EditorialSection section) {
        EditorialSectionDto dto = new EditorialSectionDto();
        dto.setId(section.getId());
        dto.setName(section.getName());
        dto.setIsAvailableForPublication(section.getIsAvailableForPublication());
        return dto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EditorialSectionDto> getAll() {
        return sectionService.findAll().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EditorialSectionDto getById(@PathVariable Long id) {
        return toDto(sectionService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditorialSectionDto create(@RequestBody EditorialSectionDto dto) {
        EditorialSection section = new EditorialSection(dto.getName(), dto.getIsAvailableForPublication());
        return toDto(sectionService.create(section));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EditorialSectionDto patch(@PathVariable Long id, @RequestBody EditorialSectionDto dto) {
        EditorialSection patch = new EditorialSection(dto.getName(), dto.getIsAvailableForPublication());
        return toDto(sectionService.patch(id, patch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        sectionService.delete(id);
    }
}