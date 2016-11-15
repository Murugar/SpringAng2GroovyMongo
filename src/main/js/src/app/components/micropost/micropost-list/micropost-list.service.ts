import {Observable} from "rxjs/Observable";
import {Injectable} from "@angular/core";
import {Micropost} from "../../../core/domains";
import {objToSearchParams} from "../../../core/services/helpers";
import {JsonHttp} from "../../../core/services";

const url = (userId: number): string => `/api/users/${userId}/microposts`;

@Injectable()
export class MicropostListService {

  constructor(private http: JsonHttp) {
  }

  list(userId: number, params: {maxId: number, count: number}): Observable<Micropost[]> {
    console.log('Micropost Service ' + url(userId), {search: objToSearchParams(params)})
    return this.http.get(url(userId), {search: objToSearchParams(params)})
      .map(res => res.json())
      ;
  }

}
