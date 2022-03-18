package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@ModelAttribute(name = "page") int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page-1, 1);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page-1, 1);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }
    @PostMapping
    public String add(@RequestBody StudentDto studentDto){
        Address address=new Address();
        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        address.setStreet(studentDto.getStreet());
        boolean b = studentRepository.existsByFirstNameAndLastNameAndAddress_Id(studentDto.getFirstName(), studentDto.getLastName(), address.getId());

        if(b){
            return "already exist";
        }
        Student student=new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setAddress(address);
        boolean b1 = groupRepository.existsById(studentDto.getGroupId());
        if(!b1){
            return "not found";
        }
        Optional<Group> byId = groupRepository.findById(studentDto.getGroupId());
        
        student.setGroup(byId.get());
        List<Subject> subjects=new ArrayList<>();
        for (Subject subject : subjectRepository.findAllById(studentDto.getSubjectsId())) {
            subjects.add(subject);
        }
        student.setSubjects(subjects);
        studentRepository.save(student);
        return "added";

    }
    @PutMapping("/edit/{id}")
    public String editById(@PathVariable Integer id,@RequestBody StudentDto studentDto){
        boolean b = studentRepository.existsById(id);
        if(!b){
            return "not found";
        }
        Optional<Student> byId = studentRepository.findById(id);
        Student student = byId.get();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        Optional<Group> byId1 = groupRepository.findById(studentDto.getGroupId());
        student.setGroup(byId1.get());
        Address address=new Address();
        address.setStreet(studentDto.getStreet());
        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        student.setAddress(address);
        List<Subject> subjects=new ArrayList<>();
        for (Subject subject : subjectRepository.findAllById(studentDto.getSubjectsId())) {
            subjects.add(subject);
        }
        student.setSubjects(subjects);
        studentRepository.save(student);
        return "edited";
    }
    @DeleteMapping("/delete/{id}")
    public String deleteByid(@PathVariable Integer id){
        Optional<Student> byId = studentRepository.findById(id);
        if(!byId.isPresent()){
            return "not found";
        }
        studentRepository.deleteById(id);
        return "deleted";
    }
    //3. FACULTY DEKANAT
    //4. GROUP OWNER


}
