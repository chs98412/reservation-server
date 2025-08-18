import http from 'k6/http';
import {check, sleep} from 'k6';
import {Trend} from 'k6/metrics';

let responseTime = new Trend('response_time', true);

export let options = {
    vus: 500,
    duration: '30s',
};

export default function () {
    let res = http.post('http://localhost:8080/reservation/top-ranked/CLASSICAL');

    responseTime.add(res.timings.duration);

    check(res, {'status is 200': (r) => r.status === 200});

    sleep(1);
}
