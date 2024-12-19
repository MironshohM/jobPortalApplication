package com.luv2code.jobportal.service;


import com.luv2code.jobportal.entity.*;
import com.luv2code.jobportal.repository.JobPostActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JobPostedActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;


    @Autowired
    public JobPostedActivityService(JobPostActivityRepository jobPostActivityRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
    }
    public JobPostActivity addNew(JobPostActivity jobPostActivity){
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobsDto> getRecruiterJobs(int recruiterId){
        List<IRecruiterJobs> iRecruiterJobs=jobPostActivityRepository.getRecruiterJobs(recruiterId);

        List<RecruiterJobsDto> recruiterJobsDtos=new ArrayList<>();

        for(var v:iRecruiterJobs){
            JobLocation j=new JobLocation(v.getLocationId(),v.getCity(),v.getState(),v.getCountry());
            JobCompany c=new JobCompany(v.getCompantId(),v.getName(),"");
            recruiterJobsDtos.add(new RecruiterJobsDto(v.getTotalCandidates(),v.getJob_post_id(),v.getJob_title(),j,c));

        }
        return recruiterJobsDtos;

    }


    public JobPostActivity getOne(int id) {

        return jobPostActivityRepository.findById(id).orElseThrow(()-> new RuntimeException("Job not found "));

    }

    public List<JobPostActivity> getAll() {
        return jobPostActivityRepository.findAll();
    }

    public List<JobPostActivity> search(String job, String location, List<String> type, List<String> remote, LocalDate searchDate) {


        return Objects.isNull(searchDate)?jobPostActivityRepository.searchWithoutDate(job,location,remote,type) :
                jobPostActivityRepository.search(job,location,remote,type,searchDate);
    }
}
