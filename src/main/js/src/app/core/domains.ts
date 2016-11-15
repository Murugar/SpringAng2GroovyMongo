export interface Micropost {
  id:number;
  content:string;
  uid:number;
  type:string;
  title:string;
  user:User;
  createdAt:number;
  isMyPost?:boolean;
}

export interface User {
  id:number;
  email:string;
  uid:number;
  name?:string;
  fname?:string;
  userStats?:UserStats;
  isMyself?:boolean;
  me:boolean;
}

export interface RelatedUser extends User {
  relationshipId:number;
}

export interface UserStats {
  micropostCnt:number;
  followingCnt:number;
  followerCnt:number;
  followedByMe:boolean;
}

