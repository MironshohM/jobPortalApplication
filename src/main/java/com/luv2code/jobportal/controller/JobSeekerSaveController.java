package com.luv2code.jobportal.controller;


import com.luv2code.jobportal.entity.JobPostActivity;
import com.luv2code.jobportal.entity.JobSeekerProfile;
import com.luv2code.jobportal.entity.JobSeekerSave;
import com.luv2code.jobportal.entity.Users;
import com.luv2code.jobportal.service.JobPostedActivityService;
import com.luv2code.jobportal.service.JobSeekerProfileService;
import com.luv2code.jobportal.service.JobSeekerSaveService;
import com.luv2code.jobportal.service.UsersService;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class JobSeekerSaveController {


    private final UsersService usersService;

    private final JobSeekerProfileService jobSeekerProfileService;

    private final JobPostedActivityService jobPostedActivityService;

    private final JobSeekerSaveService jobSeekerSaveService;


    public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService, JobPostedActivityService jobPostedActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostedActivityService = jobPostedActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AbstractAuthenticationToken)){
            String currentUsername=authentication.getName();
            Users user=usersService.findByEmail(currentUsername);
            Optional<JobSeekerProfile> seekerProfile=jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity=jobPostedActivityService.getOne(id);
            if(seekerProfile.isPresent() && jobPostActivity!=null){
                jobSeekerSave.setJob(jobPostActivity);
                jobSeekerSave.setUserId(seekerProfile.get());

            }else{
                throw new RuntimeException("User not found");
            }
            jobSeekerSaveService.addNew(jobSeekerSave);

        }
        return "redirect:/dashboard/";
    }


    @GetMapping("saved-jobs/")
    public String savedJobs(Model model){
        List<JobPostActivity> jobPostActivities=new ArrayList<>();

        Object currentuserProfile=usersService.getCurrentUserProfile();

        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getcandidateJobs((JobSeekerProfile) currentuserProfile);

        for(JobSeekerSave jobSeekerSave:jobSeekerSaveList){
            jobPostActivities.add(jobSeekerSave.getJob());
        }

        model.addAttribute("jobPost",jobPostActivities);
        model.addAttribute("user",currentuserProfile);



        return "saved-jobs";
    }




}
