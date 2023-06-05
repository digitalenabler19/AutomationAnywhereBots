
using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using System;
using System.Collections.Generic;
//--------Author-------------//
//Sikha P
//Partner Solution Desk(PSD)
//Automation Anywhere
//--------------------------//
//Use below link to get the region system name (2nd column in the table)
//https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Concepts.RegionsAndAvailabilityZones.html

namespace AWSS3Buckets
{
    public class S3UploadDownload
    {
        public string BucketName { get; set; }

        public string UploadFileName { get; set; }

        public string UploadFilePath { get; set; }

        public string DownloadFileName { get; set; }

        public string DeleteFileName { get; set; }

        public string DownloadFilePath { get; set; }

        public string KeyPairName { get; set; }

        public string CreateBucket { get; set; }

        public string AccessKeyID { get; set; }

        public string SecretKey { get; set; }

        public string Region { get; set; }


     

        public string UploadToS3()
        {
            if (string.IsNullOrEmpty(this.AccessKeyID))
                return "Please enter AccessKeyID field";
            if (string.IsNullOrEmpty(this.SecretKey))
                return "Please enter SecretKey field";
            if (string.IsNullOrEmpty(this.Region))
                return "Please enter Region field";
            RegionEndpoint regionEndpoint = RegionEndpoint.GetBySystemName(this.Region);
            if (regionEndpoint.DisplayName == "Unknown")
            {
                return "Please enter valid Region field";
            }
          
            if (string.IsNullOrEmpty(this.CreateBucket))
                return "Please enter CreateBucket field(True or False)";
            if (!this.CreateBucket.Equals("True") && !this.CreateBucket.Equals("False"))
                return "Please enter either True or False(Case Sensitive) for CreateBucket Field";
           
            if (string.IsNullOrEmpty(this.BucketName))
                return "Please enter BucketName field";
            if (string.IsNullOrEmpty(this.UploadFileName))
                return "Please enter UploadFileName field";
            AmazonS3Client amazonS3Client;
            try
            {
                amazonS3Client = new AmazonS3Client(this.AccessKeyID.Trim(), this.SecretKey.Trim(), regionEndpoint);
            }
            catch (Exception ex)
            {
                return ex.Message;
            }
            if (this.CreateBucket.Equals("True"))
            {
                try
                {
                    PutBucketRequest request = new PutBucketRequest()
                    {
                        BucketName = this.BucketName,
                        CannedACL = S3CannedACL.PublicRead
                    };
                    amazonS3Client.PutBucket(request);
                }
                catch (AmazonS3Exception ex)
                {
                    return "There was an error creating the S3 bucket. Here is the detailed message : " + ex.Message;
                }
            }
            try
            {
                PutObjectRequest request = new PutObjectRequest()
                {
                    BucketName = this.BucketName,
                    Key = this.UploadFileName,
                    FilePath = this.UploadFilePath,
                    ContentType = "text/plain"
                };
                amazonS3Client.PutObject(request);
            }
            catch (AmazonS3Exception ex)
            {
                return "There was an error uploading file to the S3 bucket. Here is the detailed message : " + ex.Message;
            }
            return "File successfully uploaded to the S3 bucket";
        }



      
        public string DownloadFromS3()
        {
           
            if (string.IsNullOrEmpty(this.AccessKeyID))
                return "Please enter AccessKeyID field";
            if (string.IsNullOrEmpty(this.SecretKey))
                return "Please enter SecretKey field";
            if (string.IsNullOrEmpty(this.Region))
                return "Please enter Region field";

            RegionEndpoint regionEndpoint = RegionEndpoint.GetBySystemName(this.Region);
            if (regionEndpoint.DisplayName == "Unknown")
            {
                return "Please enter valid Region field";
            }
          
        

            if (string.IsNullOrEmpty(this.BucketName))
                return "Please enter BucketName field";
            if (string.IsNullOrEmpty(this.DownloadFileName))
                return "Please enter DownloadFileName field";
            AmazonS3Client amazonS3Client;
            try
            {
                amazonS3Client = new AmazonS3Client(this.AccessKeyID.Trim(), this.SecretKey.Trim(), regionEndpoint);
            }
            catch (Exception ex)
            {
                return ex.Message;
            }
            try
            {
                GetObjectRequest request = new GetObjectRequest()
                {
                    BucketName = this.BucketName,
                    Key = this.DownloadFileName
                };
             amazonS3Client.GetObject(request).WriteResponseStreamToFile(this.DownloadFilePath);
            }
            catch (AmazonS3Exception ex)
            {
                return "There was an error downloading file from the S3 bucket. Here is the detailed message : " + ex.Message;
            }
            return "File successfully downloaded from the S3 bucket";
        }

     

        public string DeleteFromS3()
        {

            if (string.IsNullOrEmpty(this.AccessKeyID))
                return "Please enter AccessKeyID field";
            if (string.IsNullOrEmpty(this.SecretKey))
                return "Please enter SecretKey field";
            if (string.IsNullOrEmpty(this.Region))
                return "Please enter Region field";
            RegionEndpoint regionEndpoint = RegionEndpoint.GetBySystemName(this.Region);
            if (regionEndpoint.DisplayName == "Unknown")
            {
                return "Please enter valid Region field";
            }
           

            if (string.IsNullOrEmpty(this.BucketName))
                return "Please enter BucketName field";
            if (string.IsNullOrEmpty(this.DeleteFileName))
                return "Please enter DeleteFileName field";
            AmazonS3Client amazonS3Client;
            try
            {
                amazonS3Client = new AmazonS3Client(this.AccessKeyID.Trim(), this.SecretKey.Trim(), regionEndpoint);
            }
            catch (Exception ex)
            {
                return ex.Message;
            }
            try
            {
                DeleteObjectRequest request = new DeleteObjectRequest()
                {
                    BucketName = this.BucketName,
                    Key = this.DeleteFileName
                };
                amazonS3Client.DeleteObject(request);
            }
            catch (AmazonS3Exception ex)
            {
                return "There was an error deleting file from the S3 bucket. Here is the detailed message : " + ex.Message;
            }
            return "File successfully deleted from the S3 bucket";
        }
    }
}
