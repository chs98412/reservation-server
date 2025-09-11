import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
    vus: 50,             // 동시 사용자 100명
    duration: '3m',       // 3분간 유지
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% < 500ms
        http_req_failed: ['rate<0.01'],   // 실패율 < 1%
    },
};

const BASE_URL = 'http://localhost:8081/queue/token';

export default function () {
    const concertId = 1;
    const url = `${BASE_URL}/${concertId}`;
    const headers = {'X-ACCOUNT-ID': `user-${__VU}-${__ITER}`};

    let res = http.post(url, null, {headers});

    check(res, {'status is 200': (r) => r.status === 200});
    sleep(1);
}
