package com.servei.notifications_service;

import com.servei.notifications_service.nodes.Absence;
import com.servei.notifications_service.nodes.Notification;
import com.servei.notifications_service.nodes.Student;
import com.servei.notifications_service.nodes.Teacher;
import com.servei.notifications_service.repositories.*;
import com.servei.notifications_service.services.NotificationError;
import com.servei.notifications_service.services.NotificationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class NotificationController {

    private TeacherRepository teacherRepository;
    private NotificationRepository notificationRepository;
    private StudentRepository studentRepository;
    private ProviderRepository providerRepository;
    private AbsenceRepository absenceRepository;

    @Autowired
    public NotificationController(TeacherRepository teacherRepository,
                                  NotificationRepository notificationRepository,
                                  StudentRepository studentRepository,
                                  AbsenceRepository absenceRepository,
                                  ProviderRepository providerRepository,
                                  NotificationProvider mailProvider) {
        this.teacherRepository = teacherRepository;
        this.notificationRepository = notificationRepository;
        this.absenceRepository = absenceRepository;
        this.providerRepository = providerRepository;
        this.studentRepository = studentRepository;
        this.mailProvider = mailProvider;
    }

    private final NotificationProvider mailProvider;

    @RequestMapping("/sendmail")
    public void sendmail() {
        Teacher teacher = getTeacher();
        List<NotificationError> sent = mailProvider.sendNotifications(teacher);

        if (!sent.isEmpty()){
            //Aquí controlar errores en caso de que la lista no este vacia.
        }
    }

    @RequestMapping("/getTeacher")
    public Teacher getTeacher() {

        teacherRepository.deleteAll();
        notificationRepository.deleteAll();
        studentRepository.deleteAll();
        providerRepository.deleteAll();
        absenceRepository.deleteAll();

        DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        Absence absence1 = new Absence();
        absence1.setDate(date.format(dtfDate));
        absence1.setTime(time.format(dtfTime));
        absence1.setSubject("Desplegament");

        Absence absence2 = new Absence();
        absence2.setDate(date.format(dtfDate));
        absence2.setTime(time.format(dtfTime));
        absence2.setSubject("DIW");

        Student student1 = new Student();
        student1.setDNI("4845848468S");
        student1.setName("Berjamin");
        student1.setSurname("Cardona");

        Student student2 = new Student();
        student2.setDNI("4564678468S");
        student2.setName("Joan Guillem");
        student2.setSurname("Cabot");

        student1.hasAbsence(absence1);
        student2.hasAbsence(absence2);

        Notification notification1 = new Notification();
        notification1.setDate(date.format(dtfDate));
        notification1.setTime(time.format(dtfTime));
        notification1.setItWasSent(true);

        notification1.belongsToStudent(student1);

        Notification notification2 = new Notification();
        notification2.setDate(date.format(dtfDate));
        notification2.setTime(time.format(dtfTime));
        notification2.setItWasSent(false);

        notification2.belongsToStudent(student2);

        Teacher teacher = new Teacher();
        teacher.setDNI("45646969P");
        teacher.setMail("jgcabotd@gmail.com");
        teacher.setName("Joan");
        teacher.setSurname("Galmes");
        teacher.setPhoneNum("654887548");

        teacher.receiveNotifications(notification1);
        teacher.receiveNotifications(notification2);

        teacherRepository.save(teacher);

        return teacher;
    }

}
