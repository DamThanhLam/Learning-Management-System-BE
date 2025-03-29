//package fit.iuh.edu.com.services.Impl;
//
//import fit.iuh.edu.com.models.Teacher;
//import fit.iuh.edu.com.models.User;
//import fit.iuh.edu.com.repositories.TeacherRepository;
//import fit.iuh.edu.com.repositories.UserRepository;
//import fit.iuh.edu.com.services.BL.TeacherServiceBL;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TeacherServiceImpl implements TeacherServiceBL {
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private TeacherRepository teacherRepository;
//
//    @Override
//    public boolean beforeAddTeacher(String email, String phoneNumber) {
//        User user = userRepository.findUserByEmailOrPhoneNumber(email, phoneNumber);
//        Teacher teacherTemp = teacherRepository.findUserByEmailOrPhoneNumber(email, phoneNumber);
//        return user == null && teacherTemp == null;
//    }
//
//    @Override
//    public Teacher createTeacher(Teacher teacherTemp) {
//        return teacherRepository.create(teacherTemp);
//    }
//
//    @Override
//    public Teacher findById(String id) {
//        return teacherRepository.findById(id);
//    }
//
//    @Override
//    public void delete(Teacher teacherTemp) {
//        teacherRepository.delete(teacherTemp);
//    }
//
//    @Override
//    public void update(Teacher teacher) {
//        teacherRepository.update(teacher);
//    }
//}
