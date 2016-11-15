package com.iqmsoft.security;

import com.iqmsoft.domain.MyUser;
import java.util.Optional;

public interface SecurityContextService {
    MyUser currentUser();
  
}
