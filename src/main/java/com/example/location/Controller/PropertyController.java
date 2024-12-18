package com.example.location.Controller;

import com.example.location.Entity.PropertyEntity;
import com.example.location.Entity.UserEntity;
import com.example.location.Repository.PropertyRepository;
import com.example.location.Repository.UserRepository;
import com.example.location.Security.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> addProperty(
            @RequestParam("title") String title,
            @RequestParam("latitude") String latitude,
            @RequestParam("longitude") String longitude,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile image, // Correct parameter for the uploaded file
            Principal principal) {

        UserEntity owner = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        PropertyEntity property = new PropertyEntity(); // Ensure you create or fetch the property correctly
        property.setTitle(title);
        property.setLongitude(longitude);
        property.setLatitude(latitude);
        property.setDescription(description);
        property.setOwner(owner);

        // Check if a file is provided
        if (!image.isEmpty()) {
            try {
                // Store the file using FileStorageService
                String imageUrl = fileStorageService.storeFile(image.getBytes(), image.getOriginalFilename());
                property.setImageUrl(imageUrl);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("File upload failed: " + e.getMessage());
            }
        }

        PropertyEntity savedProperty = propertyRepository.save(property);
        return ResponseEntity.ok(savedProperty);
    }


    @GetMapping
    public List<PropertyEntity> getAllProperties() {
        return propertyRepository.findAll();
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id, Principal principal) {
        PropertyEntity property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        UserEntity owner = property.getOwner();
        if (!owner.getUsername().equals(principal.getName())) {
            return ResponseEntity.status(403).body("You are not authorized to delete this property");
        }

        propertyRepository.delete(property);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}")
    public Optional<PropertyEntity> getPropertyById(@PathVariable Long id) {
        return propertyRepository.findById(id);
    }
    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public List<PropertyEntity> getOwnerProperties(Principal principal) {
        UserEntity owner = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        return propertyRepository.findByOwner(owner);
    }
}
