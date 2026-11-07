package com.example.demo.service;

import com.example.demo.dto.AdminRequestDTO;
import com.example.demo.dto.AdminResponseDTO;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Admin;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AdminResponseDTO> getAllAdmins() {
        return adminRepository.findAll()
                .stream()
                .map(AdminResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminResponseDTO getAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));
        return AdminResponseDTO.fromEntity(admin);
    }

    public AdminResponseDTO createAdmin(AdminRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username '" + dto.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email '" + dto.getEmail() + "' is already registered");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required for creating a new Admin");
        }

        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setFullName(dto.getFullName());
        admin.setEmail(dto.getEmail());
        admin.setDepartment(dto.getDepartment());
        admin.setOfficeLocation(dto.getOfficeLocation());

        Admin saved = adminRepository.save(admin);
        return AdminResponseDTO.fromEntity(saved);
    }

    public AdminResponseDTO updateAdmin(Long id, AdminRequestDTO dto) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));

        if (!admin.getUsername().equalsIgnoreCase(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username '" + dto.getUsername() + "' is already taken");
        }
        if (!admin.getEmail().equalsIgnoreCase(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email '" + dto.getEmail() + "' is already registered");
        }

        admin.setUsername(dto.getUsername());
        admin.setFullName(dto.getFullName());
        admin.setEmail(dto.getEmail());
        admin.setDepartment(dto.getDepartment());
        admin.setOfficeLocation(dto.getOfficeLocation());

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Admin updated = adminRepository.save(admin);
        return AdminResponseDTO.fromEntity(updated);
    }

    public void deleteAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id));
        adminRepository.delete(admin);
    }
}
