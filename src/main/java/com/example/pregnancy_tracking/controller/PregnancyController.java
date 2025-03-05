package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.PregnancyDTO;
import com.example.pregnancy_tracking.dto.PregnancyStatusDTO;
import com.example.pregnancy_tracking.entity.Pregnancy;
import com.example.pregnancy_tracking.service.PregnancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pregnancies")
@CrossOrigin(origins = "*")  // Cho phép frontend truy cập API
public class PregnancyController {
    @Autowired
    private PregnancyService pregnancyService;

    @PostMapping("/create")
    public ResponseEntity<Pregnancy> createPregnancy(@Valid @RequestBody PregnancyDTO pregnancyDTO) {
        Pregnancy createdPregnancy = pregnancyService.createPregnancy(pregnancyDTO);
        return ResponseEntity.ok(createdPregnancy);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Pregnancy> getPregnancyById(@PathVariable Long id) {
        Pregnancy pregnancy = pregnancyService.getPregnancyById(id);
        return ResponseEntity.ok(pregnancy);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Pregnancy> updatePregnancy(@PathVariable Long id,
                                                     @Valid @RequestBody PregnancyDTO pregnancyDTO) {
        Pregnancy updatedPregnancy = pregnancyService.updatePregnancy(id, pregnancyDTO);
        return ResponseEntity.ok(updatedPregnancy);
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<Pregnancy> updatePregnancyStatus(@PathVariable Long id,
                                                           @RequestBody PregnancyStatusDTO statusDTO) {
        Pregnancy updatedPregnancy = pregnancyService.updatePregnancyStatus(id, statusDTO.getStatus());
        return ResponseEntity.ok(updatedPregnancy);
    }

}
