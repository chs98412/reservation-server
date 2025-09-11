import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
    scenarios: {
        peak: {
            executor: 'constant-vus',
            vus: 1000,         // 순간 트래픽
            duration: '1m',    // 1분간 집중
        },
        monitor: {
            executor: 'constant-vus',
            vus: 30,           // 이후 안정성 모니터링
            duration: '2m',
            startTime: '1m',
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<1000'],
        http_req_failed: ['rate<0.05'],
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
