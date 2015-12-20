#define _GNU_SOURCE
#include <unistd.h>
#include <sched.h>
#include <pthread.h>
#include <stdio.h>
#include <errno.h>
#include <stdint.h>
#include <string.h>
#include <sys/time.h>
#include <sys/resource.h>

const char* setAffinity(int pid, int affinity[]) {
    errno = 0;
    cpu_set_t cpuSet;
    CPU_ZERO(&cpuSet);
    for (int i = 0; i < (sizeof(affinity) / (sizeof(affinity[0])/2)); i++) {
        CPU_SET(affinity[i], &cpuSet);
    }
    sched_setaffinity(pid, sizeof(cpu_set_t), &cpuSet);
    if (errno) {
        return strerror(errno);
    }
    return "";
}

const char* setPriority(int pid, int policy, int priority) {
    errno = 0;
    if (policy == 0) {
        struct sched_param sp = {.sched_priority = 0};
        sched_setscheduler(pid, SCHED_OTHER, &sp);
        if (errno) {
            return strerror(errno);
        }
        setpriority(0, pid, priority);
        if (errno) {
            return strerror(errno);
        }
        return "";
    }
    if (policy == 1) {
        struct sched_param sp = {.sched_priority = priority};
        sched_setscheduler(pid, SCHED_FIFO, &sp);
        if (errno) {
            return strerror(errno);
        }
        return "";
    }
    if (policy == 2) {
        struct sched_param sp = {.sched_priority = priority};
        sched_setscheduler(pid, SCHED_RR, &sp);
        if (errno) {
            return strerror(errno);
        }
        return "";
    }
}

int getProcessorCount() {
    return sysconf(_SC_NPROCESSORS_ONLN);
}
